package com.example.atheneum;

import com.example.atheneum.models.Notification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationTest {
    private Notification notification;
    private String requesterID;
    private String ownerID;
    private String notificationReceiverID;
    private String bookID;
    private Notification.NotificationType rNotificationType;
    private String message;
    private boolean isSeen;

    @Before
    public void setUp() throws Exception {
        requesterID = "i8rjhrij4rjrsasf3r3r3";
        ownerID = "fj39802jfj3fr8f34j01j3jf13";
        notificationReceiverID = requesterID;
        bookID = "asdf-1234-wef-rerorerorerorero-616";
        rNotificationType = Notification.NotificationType.REQUEST;
        message = "Michael requested for your Seven Seas";
        isSeen = false;
        notification = new Notification(
                requesterID,
                ownerID,
                notificationReceiverID,
                bookID,
                rNotificationType,
                message);
        notification.setIsSeen(isSeen);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getRequesterID() {
        assertEquals(notification.getRequesterID(), requesterID);
    }

    @Test
    public void setRequesterID() {
        String newRequesterID = "hihhiaeioud3eee3aslrkifoiu432oijf";
        notification.setRequesterID(newRequesterID);
        assertEquals(notification.getRequesterID(), newRequesterID);
        notification.setRequesterID(requesterID);
        assertEquals(notification.getRequesterID(), requesterID);
    }

    @Test
    public void getOwnerID() { assertEquals(notification.getOwnerID(), ownerID); }

    @Test
    public void setOwnerID() {
        String newOwnerID = "pawn8829rdwaeriouhahahahahheheheiuhiihihihihihii";
        notification.setOwnerID(newOwnerID);
        assertEquals(notification.getOwnerID(), newOwnerID);
        notification.setOwnerID(ownerID);
        assertEquals(notification.getOwnerID(), ownerID);
    }

    @Test
    public void getNotificationReceiverID() {
        assertEquals(notification.getNotificationReceiverID(), notificationReceiverID);
    }

    @Test
    public void setNotificationReceiverID() {
        String newNotificationReceiverID = "2743ythjlkgglgloopyglopyhahahihimaplestory";
        notification.setNotificationReceiverID(newNotificationReceiverID);
        assertEquals(notification.getNotificationReceiverID(), newNotificationReceiverID);
        notification.setNotificationReceiverID(notificationReceiverID);
        assertEquals(notification.getNotificationReceiverID(), notificationReceiverID);
    }

    @Test
    public void getBookID() { assertEquals(notification.getBookID(), bookID); }

    @Test
    public void setBookID() {
        String newBookID = "testing-testing-123-haha-lmao-whatkindofidisthis";
        notification.setBookID(newBookID);
        assertEquals(notification.getBookID(), newBookID);
        notification.setBookID(bookID);
        assertEquals(notification.getBookID(), bookID);
    }

    @Test
    public void getrNotificationType() {
        assertEquals(notification.getrNotificationType(), Notification.NotificationType.REQUEST);
    }

    @Test
    public void setrNotificationType() {
        Notification.NotificationType newrNotificationType = Notification.NotificationType.ACCEPT;
        notification.setrNotificationType(newrNotificationType);
        assertEquals(notification.getrNotificationType(), newrNotificationType);
        notification.setrNotificationType(rNotificationType);
        assertEquals(notification.getrNotificationType(), rNotificationType);
    }

    @Test
    public void getMessage() {
        assertEquals(notification.getMessage(), message);
    }

    @Test
    public void setMessage() {
        String newMessage = "Juan has made a request for your copy of Pride and Prejudice!";
        notification.setMessage(newMessage);
        assertEquals(notification.getMessage(), newMessage);
        notification.setMessage(message);
        assertEquals(notification.getMessage(), message);
    }

    @Test
    public void getIsSeen() { assertFalse(notification.getIsSeen()); }

    @Test
    public void setIsSeen() {
        boolean newIsSeen = true;
        notification.setIsSeen(newIsSeen);
        assertTrue(notification.getIsSeen());
        notification.setIsSeen(isSeen);
        assertFalse(notification.getIsSeen());
    }
}