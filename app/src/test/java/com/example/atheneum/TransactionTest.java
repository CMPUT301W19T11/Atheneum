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

    private int initalType;
    private Location initialLocation;
    private User borrower;
    private User owner;
    UUID bookID;
    private Transaction transaction;

    @Before
    public void setUp() throws Exception {
        initalType = Transaction.RETURN;
        initialLocation = new Location(EDMONTON_LAT, EDMONTON_LON);
        String userID = UUID.randomUUID().toString();
        borrower = new User(userID, "borrows@borrow.com", "0123456789", 0.0, 0.0);
        owner = new User(userID, "owner@owner.com", "9876543210", 5.0, 5.0);
        bookID = UUID.randomUUID();
        transaction = new Transaction(initalType, initialLocation, borrower, owner, bookID, false,
                false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getType() {
        assertEquals(transaction.getType(), initalType);
        // Since the transaction's type doesn't have a setter, we create a new transaction in order
        // to check Transaction.getType() when we create a transaction with type Transaction.RETURN
        transaction = new Transaction(Transaction.RETURN, initialLocation, borrower, owner, bookID, false,
                false);
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
        assertThat(transaction.getBorrower(), is(borrower));
    }

    @Test
    public void getOwner() {
        assertThat(transaction.getOwner(), is(owner));
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

    @Test
    public void isComplete() {
        boolean[] oScanValues = new boolean[]{false, true};
        boolean[] bScanValues = new boolean[]{false, true};
        for (boolean oScanVal : oScanValues) {
            for (boolean bScanVal : bScanValues) {
                transaction.setOScan(oScanVal);
                transaction.setBScan(bScanVal);
                assertEquals(transaction.isComplete(), oScanVal && bScanVal);
            }
        }
    }
}