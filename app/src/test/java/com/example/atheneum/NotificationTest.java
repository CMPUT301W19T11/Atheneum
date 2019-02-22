package com.example.atheneum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationTest {
    private String initialUsername;
    private String initialMessage;
    private Notification notification;

    @Before
    public void setUp() throws Exception {
        initialUsername = "user@gmail.com";
        initialMessage = "Anthony has accepted your request for The Great Gatsby!";
        notification = new Notification(initialUsername, initialMessage);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getUsername() {
        assertEquals(notification.getUsername(), initialUsername);
    }

    @Test
    public void setUsername() {
        String newUsername = "scrub@hotmail.com";
        notification.setUsername(newUsername);
        assertEquals(notification.getUsername(), newUsername);
        notification.setUsername(initialUsername);
        assertEquals(notification.getUsername(), initialUsername);
    }

    @Test
    public void getMessage() {
        assertEquals(notification.getMessage(), initialMessage);
    }

    @Test
    public void setMessage() {
        String newMessage = "Juan has made a request for your copy of Pride and Prejudice!";
        notification.setMessage(newMessage);
        assertEquals(notification.getMessage(), newMessage);
        notification.setMessage(initialMessage);
        assertEquals(notification.getMessage(), initialMessage);
    }
}