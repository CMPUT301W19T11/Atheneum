package com.example.atheneum;

import com.google.firebase.database.PropertyName;

import java.util.UUID;

/**
 * Class used to represent transaction between an owner and borrower regarding a specific book
 * at a specified location.
 */
public class Transaction {
    int type;
    Location location;
    User borrower;
    User owner;
    UUID bookID;
    boolean bScan;
    boolean oScan;

    /**
     * Creates a new Transaction object using default values for attributes.
     *
     * Empty constructor needed for Firebase. Do not use in application code.
     */
    public Transaction() {
        type = 0;
        bScan = false;
        oScan = false;
    }

    /**
     * Creates a new Transaction object using specified values for attributes
     *
     * @param type Determines type of transaction (ie. checkout or return)
     * @param location Location where transaction will occur
     * @param borrower User who is borrowing book
     * @param owner User who owns the book
     * @param bookID Identifier of book
     * @param bScan Represents if the borrower has scanned the book
     * @param oScan Represents if the owner has scanned the book
     */
    public Transaction(int type, Location location, User borrower, User owner, UUID bookID, boolean bScan, boolean oScan) {
        this.type = type;
        this.location = location;
        this.borrower = borrower;
        this.owner = owner;
        this.bookID = bookID;
        this.bScan = bScan;
        this.oScan = oScan;
    }

    /**
     *
     * Note: No setter provided since type can't change
     *
     * @return Type of transaction (ie. checkout or return)
     */
    public int getType() {
        return type;
    }

    /**
     *
     * @return Location of the transaction
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @param location New location of transaction
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     *
     * Note: No setter provided since borrower can't change
     *
     * @return Borrower of the book associated with the transaction
     */
    public User getBorrower() {
        return borrower;
    }

    /**
     *
     * Note: No setter provided since owner change can't change
     *
     * @return Owner of the book associated with the transaction
     */
    public User getOwner() {
        return owner;
    }

    /**
     *
     * Note: No setter provided since bookID can't change
     *
     * @return ID of book involved in transaction
     */
    public UUID getBookID() {
        return bookID;
    }

    /**
     *
     * @return True if borrower has scanned the book, false otherwise
     */
    @PropertyName("bScan") // Used annotation to ensure Firebase properly recognizes this method
    public boolean getBScan() {
        return bScan;
    }

    /**
     *
     * @param bScan New value of bScan
     */
    @PropertyName("bScan") // Used annotation to ensure Firebase properly recognizes this method
    public void setBScan(boolean bScan) {
        this.bScan = bScan;
    }

    /**
     *
     * @return True if owner has scanned the book, false otherwise
     */
    @PropertyName("oScan") // Used annotation to ensure Firebase properly recognizes this method
    public boolean getOScan() {
        return oScan;
    }

    /**
     *
     * @param oScan New value of oScan
     */
    @PropertyName("bScan") // Used annotation to ensure Firebase properly recognizes this method
    public void setOScan(boolean oScan) {
        this.oScan = oScan;
    }

    /**
     *
     * @return True if the transaction is complete (both owner and borrower have scanned the book,
     *         false otherwise
     */
    public boolean isComplete() {
        return oScan && bScan;
    }
}
