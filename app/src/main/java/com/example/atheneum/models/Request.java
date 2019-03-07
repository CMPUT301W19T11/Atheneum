package com.example.atheneum.models;

/**
 * The Request class for requests made of owner books by borrowers.
 */
public class Request {
    private String requesterID;
    private String bookID;
    private Status rStatus;

    /**
     * The enum Status.
     */
    public enum Status {
        /**
         * Pending status.
         */
        PENDING,
        /**
         * Accepted status.
         */
        ACCEPTED,
        /**
         * Declined status.
         */
        DECLINED
    }

    /**
     * Instantiates a new Request.
     */
    public Request() {
        this.rStatus = Status.PENDING;
    }

    /**
     * Instantiates a new Request with the requester User.
     *
     * @param requester the requester
     */
    public Request(User requester, String bookID) {
        this.requesterID = requester.getUserID();
        this.bookID = bookID;
        this.rStatus = Status.PENDING;
    }

    /**
     * Gets the requester UserID.
     *
     * @return the requester UserID
     */
    public String getRequesterID() {
        return this.requesterID;
    }

    /**
     * Gets the unique book id.
     *
     * @return the unique book id
     */
    public String getBookID() {
        return this.bookID;
    }

    /**
     * Gets the status of the request
     *
     * @return the status of the request
     */
    public Status getrStatus() {
        return this.rStatus;
    }

    /**
     * Sets the requester UserID.
     *
     * @param requester the requester
     */
    public void setRequesterID(User requester) {
        this.requesterID = requester.getUserID();
    }

    /**
     * Sets the unique book id.
     *
     * @param bookID the unique book id
     */
    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    /**
     * Sets status of the request
     *
     * @param status the status of the request
     */
    public void setrStatus(Status status) {
        this.rStatus = status;
    }

    /**
     * Accept the request
     */
    public void acceptRequest() {
        this.rStatus = Status.ACCEPTED;
    }

    /**
     * Deny the request.
     */
    public void denyRequest() {
        this.rStatus = Status.DECLINED;
    }
}
