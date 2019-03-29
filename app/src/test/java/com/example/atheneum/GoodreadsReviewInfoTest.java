package com.example.atheneum;

import com.example.atheneum.models.GoodreadsReviewInfo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GoodreadsReviewInfoTest {
    private long initial_isbn = Long.parseLong("1234567890123"); // because just writing the number out defaults to integer
    private float initial_rating = (float) 3.5;
    private String initial_url = "test.com";

    private long new_isbn = Long.parseLong("1111111111111"); // because just writing the number out defaults to integer
    private float new_rating = (float) 1.0;
    private String new_url = "changedurl.com";

    private GoodreadsReviewInfo goodreadsReviewInfo;

    @Before
    public void setup() {
        goodreadsReviewInfo = new GoodreadsReviewInfo(initial_isbn, initial_rating, initial_url);
    }

    @Test
    public void isbnTest() {
        assertEquals(goodreadsReviewInfo.getIsbn(), initial_isbn);
        goodreadsReviewInfo.setIsbn(new_isbn);
        assertEquals(goodreadsReviewInfo.getIsbn(), new_isbn);
    }

    @Test
    public void ratingTest() {
        assertTrue(goodreadsReviewInfo.getAvg_rating() == initial_rating);
        goodreadsReviewInfo.setAvg_rating(new_rating);
        assertTrue(goodreadsReviewInfo.getAvg_rating() == new_rating);
    }

    @Test
    public void urlTest() {
        assertEquals(goodreadsReviewInfo.getReviews_widget_url(), initial_url);
        goodreadsReviewInfo.setReviews_widget_url(new_url);
        assertEquals(goodreadsReviewInfo.getReviews_widget_url(), new_url);
    }
}
