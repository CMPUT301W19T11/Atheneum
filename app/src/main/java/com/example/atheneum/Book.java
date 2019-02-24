package com.example.atheneum;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * The Book class.
 */
public class Book {
    private long isbn;
    private String title = null;
    private String description = null;
    private String author = null;
    private User owner = null;
    private User borrower = null;
    private Status status = null;
    private ArrayList<Request> requests = new ArrayList<Request>();
    private UUID bookID = null;
    private ArrayList<String> photos = new ArrayList<String>();

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
     * Instantiates a new Book with specified arguments.
     */
    public Book(long isbn, String title, String description, String author, User owner,
                User borrower, Status status) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
        this.owner = owner;
        this.borrower = borrower;
        this.status = status;
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

    /**
     * Returns whether or not two Book objects are equal. For the purposes of determining equality,
     * only the UUID(bookID) is checked, as the UUID should be unique across different copies of the
     * same book.
     *
     * @param o the object to be compared with
     * @return whether or not the current object is equal to o. Returns false of o is null, or not a book object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return (this.getBookID().equals(book.getBookID()));
    }

}
