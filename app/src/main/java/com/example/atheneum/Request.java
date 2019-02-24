package com.example.atheneum;

import java.util.UUID;

/**
 * The Request class for requests made of owner books by borrowers.
 */
public class Request {
    private User requester;
    private UUID bookID;
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
    public Request(User requester, UUID bookID) {
        this.requester = requester;
        this.bookID = bookID;
        this.rStatus = Status.PENDING;
    }

    /**
     * Gets the requester User.
     *
     * @return the requester User
     */
    public User getRequester() {
        return this.requester;
    }

    /**
     * Gets the unique book id.
     *
     * @return the unique book id
     */
    public UUID getBookID() {
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
     * Sets the requester User.
     *
     * @param requester the requester
     */
    public void setRequester(User requester) {
        this.requester = requester;
    }

    /**
     * Sets the unique book id.
     *
     * @param bookID the unique book id
     */
    public void setBookID(UUID bookID) {
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
