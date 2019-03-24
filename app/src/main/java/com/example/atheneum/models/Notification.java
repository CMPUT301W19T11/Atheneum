package com.example.atheneum.models;

import java.util.Date;
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
    private boolean isSeen;
    private Date creationDate = new Date();

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
        REQUEST,
        /**
         * Decline notification type.
         */
        DECLINE
    }

    /**
     * Creates a new Notification object with default values.
     * <p>
     * Needed for Firebase. Do not use in application code.
     */
    public Notification() {
        this.notificationID = UUID.randomUUID().toString();
        this.requesterID = "";
        this.ownerID = "";
        this.notificationReceiverID = "";
        this.bookID = "";
        this.isSeen = false;
        this.rNotificationType = null;
        this.message = "";
    }

    /**
     * Instantiates a new Notification.
     *
     * @param requesterID            book requester id
     * @param ownerID                book owner id
     * @param notificationReceiverID notification receiver id
     * @param bookID                 book id
     * @param rNotificationType      notification type
     * @param message                message
     */
    public Notification(String requesterID, String ownerID,
                        String notificationReceiverID, String bookID,
                        NotificationType rNotificationType, String message,
                        boolean isSeen) {
        this.notificationID = UUID.randomUUID().toString();
        this.requesterID = requesterID;
        this.ownerID = ownerID;
        this.notificationReceiverID = notificationReceiverID;
        this.bookID = bookID;
        this.rNotificationType = rNotificationType;
        this.message = message;
        this.isSeen = isSeen;
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
     * Returns message.
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

    /**
     * Constructs the message
     */
    public void constructMessage(String userName, String bookName) {
        if (this.rNotificationType == NotificationType.REQUEST) {
            this.message = userName + " has requested for your book: " + bookName;
        } else if (this.rNotificationType == NotificationType.ACCEPT) {
            this.message = userName + " has accepted your request for the book " + bookName;
        } else if (this.rNotificationType == NotificationType.DECLINE) {
            this.message = userName + " has declined your request for the book " + bookName;
        }
    }

    /**
     * Returns isSeen boolean
     *
     * @return boolean indicating whether user has clicked this notification
     */
    public boolean getSeen() {
        return isSeen;
    }

    /**
     * Sets isSeen
     *
     * @param seen
     */
    public void setSeen(boolean seen) {
        this.isSeen = seen;
    }

    /**
     * Gets creation date of notification
     *
     * @return creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }
}
