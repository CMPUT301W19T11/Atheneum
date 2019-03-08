package com.example.atheneum.models;

import java.util.UUID;

/**
 * Represents the data contained in a push notification sent to a user.
 */
public class Notification {
    private String notificationID;
    private String requesterID;
    private String ownerID;
    private String notificationReceiverID;
    private String bookID;
    private NotificationType rNotificationType;
    private String message;

    /**
     * The enum Notification type.
     */
    public enum NotificationType {
        /**
         * Accept notification type.
         */
        ACCEPT,
        /**
         * Request notification type.
         */
        REQUEST
    }

    /**
     * Creates a new Notification object with default values.
     * <p>
     * Needed for Firebase. Do not use in application code.
     */
    public Notification() {
        notificationID = UUID.randomUUID().toString();;
        requesterID = "";
        ownerID = "";
        bookID = "";
    }

    /**
     * Instantiates a new Notification.
     *
     * @param requesterID       the requester id
     * @param ownerID           the owner id
     * @param bookID            the book id
     * @param rNotificationType the r notification type
     * @param message           the message
     */
    public Notification(String requesterID, String ownerID,
                        String notificationReceiverID, String bookID,
                        NotificationType rNotificationType, String message) {
        this.notificationID = UUID.randomUUID().toString();
        this.requesterID = requesterID;
        this.ownerID = ownerID;
        this.notificationReceiverID = notificationReceiverID;
        this.bookID = bookID;
        this.rNotificationType = rNotificationType;
        this.message = message;
    }

    /**
     * Gets notification id.
     *
     * @return the notification id
     */
    public String getNotificationID() {
        return notificationID;
    }

    /**
     * Sets notification id.
     *
     * @param notificationID the notification id
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Gets requester id.
     *
     * @return the requester id
     */
    public String getRequesterID() {
        return requesterID;
    }

    /**
     * Sets requester id.
     *
     * @param requesterID the requester id
     */
    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    /**
     * Gets owner id.
     *
     * @return the owner id
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets owner id.
     *
     * @param ownerID the owner id
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Gets notification receiver id.
     *
     * @return the notification receiver id
     */
    public String getNotificationReceiverID() {
        return notificationReceiverID;
    }

    /**
     * Sets notification receiver id.
     *
     * @param notificationReceiverID the notication receiver id
     */
    public void setNotificationReceiverID(String notificationReceiverID) {
        this.notificationReceiverID = notificationReceiverID;
    }

    /**
     * Gets book id.
     *
     * @return the book id
     */
    public String getBookID() {
        return bookID;
    }

    /**
     * Sets book id.
     *
     * @param bookID the book id
     */
    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    /**
     * Gets notification type.
     *
     * @return the notification type
     */
    public NotificationType getrNotificationType() {
        return rNotificationType;
    }

    /**
     * Sets notification type.
     *
     * @param rNotificationType the r notification type
     */
    public void setrNotificationType(NotificationType rNotificationType) {
        this.rNotificationType = rNotificationType;
    }

    /**
     * Construct and returns message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
