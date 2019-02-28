package com.example.atheneum.models;

/**
 * Represents the data contained in a push notification sent to a user.
 */
public class Notification {
    private String username;
    private String message;

    /**
     * Creates a new Notification object with default values.
     * <p>
     * Needed for Firebase. Do not use in application code.
     */
    public Notification() {
        username = "";
        message = "";
    }

    /**
     * Create a new Notification object using specified values for attributes.
     *
     * @param username Username of user to send notification to.
     * @param message  Message contained within the notification.
     */
    public Notification(String username, String message) {
        this.username = username;
        this.message = message;
    }

    /**
     * @return Username of user to send message to
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates username of the notification
     *
     * @param username New value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Message contained within the notification
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message New value of message to update notification to
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
