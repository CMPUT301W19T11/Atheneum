package com.example.atheneum.models;

/**
 * Model class for holding the goodreads review info.
 */
public class GoodreadsReviewInfo {
    private long isbn;
    private double avg_rating;
    private String reviews_widget_html;

    /**
     * Default empty constructor. Sets the average rating to 0 and review url to null
     */
    public GoodreadsReviewInfo() {
        this.isbn = Book.INVALILD_ISBN;
        this.avg_rating = 0.0;
        this.reviews_widget_html = null;
    }

    /**
     * Constructor for a GoodreadsReviewInfo object.
     *
     * @param avg_rating the book's average rating
     * @param reviews_url the url of the iframe for goodreads review widget
     */
    public GoodreadsReviewInfo(long isbn, double avg_rating, String reviews_url) {
        this.isbn = isbn;
        this.avg_rating = avg_rating;
        this.reviews_widget_html = reviews_url;
    }

    /**
     * Get the average rating for the book
     * @return the goodreads average rating
     */
    public double getAvg_rating() {
        return avg_rating;
    }

    /**
     * Sets the average rating of the book
     * @param avg_rating the value to set the rating to
     */
    public void setAvg_rating(double avg_rating) {
        this.avg_rating = avg_rating;
    }

    /**
     * Get the url string of the reviews widget taken from the goodreads api result
     * @return
     */
    public String getReviews_widget_html() {
        return reviews_widget_html;
    }

    /**
     * set the url of the goodreads reviews widget
     * @param reviews_widget_html
     */
    public void setReviews_widget_html(String reviews_widget_html) {
        this.reviews_widget_html = reviews_widget_html;
    }

    /**
     * get the isbn of the book that the review info is for
     * @return
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
}
