package com.example.atheneum.models;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * The Book class.
 */
public class Book {
    public static final long INVALILD_ISBN = -1;

    private long isbn;
    private String title = "";
    private String description = "";
    private String author = "";
    // owner and borrower are represented by the user ID
    private String ownerID = "";
    private String borrowerID = "";

    private Status status = Status.AVAILABLE;
    private ArrayList<Request> requests = new ArrayList<Request>();
    private String bookID;

    @Override
    public String toString() {
        return "Book{" +
                "isbn=" + isbn +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", borrowerID='" + borrowerID + '\'' +
                ", status=" + status +
                ", bookID='" + bookID + '\'' +
                '}';
    }

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
        this.bookID = UUID.randomUUID().toString();
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
        this.ownerID = owner == null ? "" : owner.getUserID();
        this.borrowerID = borrower == null ? "" : borrower.getUserID();
        this.status = status;
        this.bookID = UUID.randomUUID().toString();
    }

    /**
     * Instantiates a new Book with specified arguments.
     */
    public Book(long isbn, String title, String description, String author, String ownerID,
                String borrowerID, Status status) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
        this.ownerID = ownerID == null ? "" : ownerID;
        this.borrowerID = borrowerID == null ? "" : borrowerID;
        this.status = status;
        this.bookID = UUID.randomUUID().toString();
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
     * Gets ownerID of the book
     *
     * @return the owner's user ID
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets ownerID of the book
     *
     * @param ownerID the User object's string ID
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Gets borrowerID of the book
     *
     * @return the borrower's user ID
     */
    public String getBorrowerID() {
        return borrowerID;
    }


    /**
     * Sets borrowerID of the book
     *
     * @param borrowerID the borrower's string user ID
     */
    public void setBorrowerID(String borrowerID) {
        this.borrowerID = borrowerID;
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
    public String getBookID() {
        return bookID;
    }

    /**
     * Sets unique book id.
     *
     * @param bookID A string represetnation of the book ID
     */
    public void setBookID(String bookID) { this.bookID = bookID; }

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
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return getIsbn() == book.getIsbn() &&
                Objects.equals(getTitle(), book.getTitle()) &&
                Objects.equals(getDescription(), book.getDescription()) &&
                Objects.equals(getAuthor(), book.getAuthor()) &&
                Objects.equals(getOwnerID(), book.getOwnerID()) &&
                Objects.equals(getBorrowerID(), book.getBorrowerID()) &&
                getStatus() == book.getStatus() &&
                Objects.equals(getBookID(), book.getBookID());
    }

    /**
     * @return Hash of the book object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getIsbn(), getTitle(), getDescription(), getAuthor(), getOwnerID(), getBorrowerID(), getStatus(), getBookID());
    }
}
