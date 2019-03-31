package com.example.atheneum.models;

/**
 * Model class for holding the goodreads review info.
 */
public class GoodreadsReviewInfo {
    public static final float INVALID_RATING = -1;

    private long isbn10;
    private long isbn;
    private float avg_rating;
    private String reviews_widget_url;

    /**
     * Default empty constructor. Sets the average rating to 0 and review url to null
     */
    public GoodreadsReviewInfo() {
        this.isbn = Book.INVALILD_ISBN;
        this.isbn10 = Book.INVALILD_ISBN;
        this.avg_rating = INVALID_RATING;
        this.reviews_widget_url = null;
    }

    /**
     * Constructor for a GoodreadsReviewInfo object.
     *
     * @param avg_rating the book's average rating
     * @param reviews_url the url of the iframe for goodreads review widget
     */
    public GoodreadsReviewInfo(long isbn, long isbn10, float avg_rating, String reviews_url) {
        this.isbn = isbn;
        this.isbn10 = isbn10;
        this.avg_rating = avg_rating;
        this.reviews_widget_url = reviews_url;
    }

    /**
     * Get the average rating for the book
     * @return the goodreads average rating
     */
    public float getAvg_rating() {
        return avg_rating;
    }

    /**
     * Sets the average rating of the book
     * @param avg_rating the value to set the rating to
     */
    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    /**
     * Get the url string of the reviews widget taken from the goodreads api result
     * @return
     */
    public String getReviews_widget_url() {
        return reviews_widget_url;
    }

    /**
     * set the url of the goodreads reviews widget
     * @param reviews_widget_url
     */
    public void setReviews_widget_url(String reviews_widget_url) {
        this.reviews_widget_url = reviews_widget_url;
    }

    /**
     * get the isbn of the book that the review info is for
     * @return the isbn (13)
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * set the isbn of the reviews the book is for
     * @param isbn the isbn to be set
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * get the isbn10 of the book that the review info is for
     * @return the 10 digit isbn
     */
    public long getIsbn10() {
        return isbn10;
    }

    /**
     * set the isbn10 of the reviews the book is for
     * @param isbn10
     */
    public void setIsbn10(long isbn10) {
        this.isbn10 = isbn10;
    }
}
