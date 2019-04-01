package com.example.atheneum.utils;

import android.util.Log;
import android.util.Xml;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.GoodreadsReviewInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter class for converting the XML response from goodreads into a GoodreadsReveiwInfo Object.
 * Most of the XML handling taken from the Google tutorial found at
 * https://developer.android.com/training/basics/network-ops/xml
 */
public class GoodreadsReviewAdapter {
    private static final String TAG = "GoodreadsReviewAdapter";

    private String responseXML;
    private GoodreadsReviewInfo reviewInfo = null;

    /**
     * Constructor for the object
     *
     * @param xml the XML response string
     */
    public GoodreadsReviewAdapter(String xml) {
        this.responseXML = xml;
        try {
            this.reviewInfo = xmlToReviewInfo();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            this.reviewInfo = new GoodreadsReviewInfo();
        }

    }

    /**
     * Intializes the parser and calls the readFeed function to read the xml
     *
     * @return The GoodreadsReviewInfo object generated from the XML string
     * @throws XmlPullParserException
     * @throws IOException
     */
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

    /**
     * Readss the data in the parser and calls readBookEntry on the first book found, which should
     * be the desired result
     *
     * @param parser the XmlPullParser being used
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
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

    /**
     * skips the current take and moves the parser to the next one
     *
     * @param parser the XmlPullParser being used
     * @throws XmlPullParserException
     * @throws IOException
     */
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

    /**
     * Reads and returns the text between the start and end tags
     *
     * @param parser the XmlPullParser being used
     * @return the text between the tags
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Reads the data from within a book entry. Currently, takes the ISBN, average rating, and
     * the src URL of the reviews widget
     *
     * @param parser the XmlPullParser being used
     * @return the {@link GoodreadsReviewInfo} object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private GoodreadsReviewInfo readBookEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        long isbn = Book.INVALILD_ISBN;
        long isbn10 = Book.INVALILD_ISBN;
        float avg_rating = GoodreadsReviewInfo.INVALID_RATING;
        String reviews_widget_url = null;

        parser.require(XmlPullParser.START_TAG, null, "book");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("isbn13")) {
                isbn = readIsbn13(parser);
            }
            else if (name.equals("isbn")) {
                isbn10 = readIsbn10(parser);
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


        return new GoodreadsReviewInfo(isbn, isbn10, avg_rating, reviews_widget_url);
    }

    /**
     * Takes the found isbn13 tag and returns the ISBN
     *
     * @param parser the XmlPullParser being used
     * @return the ISBN
     * @throws IOException
     * @throws XmlPullParserException
     */
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

    /**
     * Takes the found isbn tag and returns the ISBN (10)
     *
     * @param parser the XmlPullParser being used
     * @return the ISBN
     * @throws IOException
     * @throws XmlPullParserException
     */
    private long readIsbn10(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "isbn");
        String isbn_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "isbn");

        try{
            return Long.parseLong(isbn_str);
        }catch (Exception e) {

            return Book.INVALILD_ISBN;
        }
    }

    /**
     * Takes the found average_rating tag and returns the average rating
     *
     * @param parser the XmlPullParser being used
     * @return the average rating
     * @throws IOException
     * @throws XmlPullParserException
     */
    private float readAvgRatng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "average_rating");
        String rating_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "average_rating");

        try{
            return Float.parseFloat(rating_str);
        }catch (Exception e) {
            return Book.INVALILD_ISBN;
        }
    }

    /**
     * Get the review widget html once the reviews_widget tag was found
     *
     * @param parser the XmlPullParser being used
     * @return The review widget html
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readReviewWidgetHtml(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "reviews_widget");
        String reviewWidgetHtml_str = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "reviews_widget");
        return reviewWidgetHtml_str;
    }

    /**
     * Uses RE to match and extract the src URL from the reviews widget HTML string
     * @param reviewWidgetHtml
     * @return the URL of the reviews
     */
    private String getReviewsUrl(String reviewWidgetHtml) {
        String url = null;
        Pattern pattern = Pattern.compile("(?:src=\\\")([^\"]+)(?:\\\")");
        Matcher matcher = pattern.matcher(reviewWidgetHtml);

        if (matcher.find()) {
            url = matcher.group(1);
        }
        return url;
    }

    /**
     * Get the reviewInfo(of type {@link GoodreadsReviewInfo}
     *
     * @return the {@link GoodreadsReviewInfo} object
     */
    public GoodreadsReviewInfo getReviewInfo() {
        return reviewInfo;
    }
}
