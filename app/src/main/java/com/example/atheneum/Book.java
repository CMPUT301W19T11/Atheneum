package com.example.atheneum;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The Book class.
 */
public class Book {
    private long isbn;
    private String title;
    private String description;
    private String author;
    private User owner;
    private User borrower;
    private Status status;
    private ArrayList<Request> requests; //Request represents our Request class. NOT java's!
    private UUID bookID;
    private ArrayList<String> photos;

    /**
     * The enum Status to show the global status of the book.
     */
    public enum Status {
        /**
         * Available status.
         */
        AVAILABLE,
        /**
         * Requested status.
         */
        REQUESTED,
        /**
         * Accepted status.
         */
        ACCEPTED,
        /**
         * Borrowed status.
         */
        BORROWED
    }

    /**
     * Instantiates a new Book.
     */
    public Book() {
        this.bookID = UUID.randomUUID();
    }

    /**
     * Gets isbn of the book
     *
     * @return the isbn
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * Sets isbn of the book
     *
     * @param isbn the isbn
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets title of the book
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of the book
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description of the book
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of the book
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets author of the book
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author of the book
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets owner of the book
     *
     * @return the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets owner of the book
     *
     * @param owner the owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets borrower of the book
     *
     * @return the borrower
     */
    public User getBorrower() {
        return borrower;
    }

    /**
     * Sets borrower of the book
     *
     * @param borrower the borrower
     */
    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    /**
     * Gets global status of the book
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets global status of the book
     *
     * @param status the status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets requests made for the book
     *
     * @return the requests
     */
    public ArrayList<Request> getRequests() {
        return requests;
    }

    /**
     * Sets requests made for the book
     *
     * @param requests the requests
     */
    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    /**
     * Add a request to the list of requests for the book
     *
     * @param request the request
     */
    public void addRequest(Request request) {
        this.requests.add(request);
    }

    /**
     * Delete a request from the list of requests for the book
     *
     * @param request the request
     */
    public void deleteRequest(Request request) {
        this.requests.remove(request);
    }

    /**
     * Delete all requests from the list of requests for the book
     */
    public void deleteRequests() {
        this.requests.clear();
    }

    /**
     * Gets unique book id.
     *
     * @return the book id
     */
    public UUID getBookID() {
        return bookID;
    }

    /**
     * Gets photos for the book
     *
     * @return the photos
     */
    public ArrayList<String> getPhotos() {
        return photos;
    }

    /**
     * Sets photos for the book
     *
     * @param photos the photos
     */
    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    /**
     * Add photo to the book
     *
     * @param photo the photo
     */
    public void addPhoto(String photo) {
        this.photos.add(photo);
    }

    /**
     * Delete photo from the book
     *
     * @param photo the photo
     */
    public void deletePhoto(String photo) {
        this.photos.remove(photo);
    }

    /**
     * Delete all photos from the book
     */
    public void deletePhotos() {
        this.photos.clear();
    }
}
