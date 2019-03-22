package com.example.atheneum.models;

import android.util.Patterns;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter class for converting the XML response from goodreads into a GoodreadsReveiwInfo Object.
 */
public class GoodreadsReviewAdapter {
    private String responseXML;
    private GoodreadsReviewInfo reviewInfo = null;

    public GoodreadsReviewAdapter(String xml) {
        this.responseXML = xml;
        this.reviewInfo = xmlToReviewInfo();
    }

    private GoodreadsReviewInfo xmlToReviewInfo() throws XmlPullParserException, IOException {
        StringReader input = new StringReader(this.responseXML);

        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(input);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            input.close();
        }
    }

    private GoodreadsReviewInfo readFeed(XmlPullParser  parser) throws XmlPullParserException, IOException {
        GoodreadsReviewInfo reviewInfo = null;// = new GoodreadsReviewInfo();

        parser.require(XmlPullParser.START_TAG, null, "GoodreadsResponse");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("book")) {
                reviewInfo = readBookEntry(parser);
            } else {
                skip(parser);
            }
        }
        return reviewInfo;

    }

    // taken from https://developer.android.com/training/basics/network-ops/xml#skip
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private GoodreadsReviewInfo readBookEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
//        GoodreadsReviewInfo goodreadsReviewInfo = new GoodreadsReviewInfo();
        long isbn = Book.INVALILD_ISBN;
        double avg_rating = GoodreadsReviewInfo.INVALID_RATING;
        String reviews_widget_url = null;

        parser.require(XmlPullParser.START_TAG, null, "book");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("isbn13")) {
                isbn = readIsbn13(parser);
            } else if (name.equals("average_rating")) {
                avg_rating = readAvgRatng(parser);
            } else if (name.equals("reviews_widget")) {
                String reviews_widget_html = null;
                reviews_widget_html = readReviewWidgetHtml(parser);
                reviews_widget_url = getReviewsUrl(reviews_widget_html);
            } else {
                skip(parser);
            }
        }


        return new GoodreadsReviewInfo(isbn, avg_rating, reviews_widget_url);
    }

    private long readIsbn13(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "isbn13");
        String isbn_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "isbn13");

        try{
            return Long.parseLong(isbn_str);
        }catch (Exception e) {
            return Book.INVALILD_ISBN;
        }
    }

    private double readAvgRatng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "average_rating");
        String rating_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "average_rating");

        try{
            return Double.parseDouble(rating_str);
        }catch (Exception e) {
            return Book.INVALILD_ISBN;
        }
    }

    private String readReviewWidgetHtml(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "reviews_widget");
        String reviewWidgetHtml_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "reviews_widget");
        return reviewWidgetHtml_str;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String getReviewsUrl(String reviewWidgetHtml) {
        String url = null;
        Pattern pattern = Pattern.compile("(?:src=\\\")([^\"]+)(?:\\\")");
        Matcher matcher = pattern.matcher(reviewWidgetHtml);

        if (matcher.find()) {
            url = matcher.group(1);
        }
        return url;
    }

    public GoodreadsReviewInfo getReviewInfo() {
        return reviewInfo;
    }
}
