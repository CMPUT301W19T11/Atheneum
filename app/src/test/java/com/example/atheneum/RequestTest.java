package com.example.atheneum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

public class RequestTest {
    private User requester;
    private Request newRequest;
    private UUID bookID;

    @Before
    public void setUp() throws Exception {
        requester = new User();
        bookID = UUID.randomUUID();
        newRequest = new Request(requester, bookID);
    }

    @Test
    public void getRequester() {
        Assert.assertThat(requester, is(newRequest.getRequester()));
    }

    @Test
    public void getBookID() {
        Assert.assertThat(newRequest.getBookID(), is(bookID));
    }

    @Test
    public void getrStatus() {
        assertEquals(Request.Status.PENDING, newRequest.getrStatus());
    }

    @Test
    public void setRequester() {
        User newRequester = new User();
        newRequest.setRequester(newRequester);
        Assert.assertThat(newRequester, is(newRequest.getRequester()));
    }

    @Test
    public void setBookID() {
        UUID newID = UUID.randomUUID();
        newRequest.setBookID(newID);
        assertEquals(newID, newRequest.getBookID());
    }

    @Test
    public void setrStatus() {
        newRequest.setrStatus(Request.Status.ACCEPTED);
        assertEquals(Request.Status.ACCEPTED, newRequest.getrStatus());
    }

    @Test
    public void acceptRequest() {
        newRequest.acceptRequest();
        assertEquals(Request.Status.ACCEPTED, newRequest.getrStatus());
    }

    @Test
    public void denyRequest() {
        newRequest.denyRequest();
        assertEquals(Request.Status.DECLINED, newRequest.getrStatus());
    }
}
