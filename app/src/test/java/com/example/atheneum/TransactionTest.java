package com.example.atheneum;

import com.example.atheneum.models.Location;
import com.example.atheneum.models.Transaction;
import com.example.atheneum.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TransactionTest {
    private static final double EDMONTON_LAT = 53.5444;
    private static final double EDMONTON_LON = 113.4909;
    private static final double CALGARY_LAT = 51.0486;
    private static final double CALGARY_LON = 114.0708;

    private String initialType;
    private Location initialLocation;
    private String borrower;
    private String owner;
    String bookID;
    private Transaction transaction;

    @Before
    public void setUp() throws Exception {
        initialType = Transaction.RETURN;
        initialLocation = new Location(EDMONTON_LAT, EDMONTON_LON);
        borrower = UUID.randomUUID().toString();
        owner = UUID.randomUUID().toString();
        bookID = UUID.randomUUID().toString();
        transaction = new Transaction(initialType, initialLocation, borrower, owner, bookID, false,
                false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getType() { assertEquals(transaction.getType(), initialType); }

    @Test
    public void setType(){
        transaction.setType(Transaction.RETURN);
        assertEquals(transaction.getType(), Transaction.RETURN);
    }

    @Test
    public void getLocation() {
        assertEquals(transaction.getLocation(), initialLocation);
    }

    @Test
    public void setLocation() {
        Location newLocation = new Location(CALGARY_LAT, CALGARY_LON);
        transaction.setLocation(newLocation);
        assertEquals(transaction.getLocation(), newLocation);
        transaction.setLocation(initialLocation);
        assertEquals(transaction.getLocation(), initialLocation);
    }

    @Test
    public void getBorrower() {
        assertThat(transaction.getBorrowerID(), is(borrower));
    }

    @Test
    public void setBorrower() {
        String newBorrower = UUID.randomUUID().toString();
        transaction.setBorrowerID(newBorrower);
        assertThat(transaction.getBorrowerID(), is(newBorrower));
    }

    @Test
    public void getOwner() {
        assertThat(transaction.getOwnerID(), is(owner));
    }

    @Test
    public void setOwner() {
        String newOwner = UUID.randomUUID().toString();
        transaction.setOwnerID(newOwner);
        assertThat(transaction.getOwnerID(), is(newOwner));
    }

    @Test
    public void getBookID() {
        assertEquals(transaction.getBookID(), bookID);
    }

    @Test
    public void getBScan() {
        assertEquals(transaction.getBScan(), false);
    }

    @Test
    public void setBScan() {
        transaction.setBScan(true);
        assertEquals(transaction.getBScan(), true);
        transaction.setBScan(false);
        assertEquals(transaction.getBScan(), false);
    }

    @Test
    public void getOScan() {
        assertEquals(transaction.getOScan(), false);
    }

    @Test
    public void setOScan() {
        transaction.setOScan(true);
        assertEquals(transaction.getOScan(), true);
        transaction.setOScan(false);
        assertEquals(transaction.getOScan(), false);
    }

}