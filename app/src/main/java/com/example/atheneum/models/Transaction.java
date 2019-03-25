package com.example.atheneum.models;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

/**
 * Class used to represent transaction between an owner and borrower regarding a specific book
 * at a specified location.
 */
public class Transaction {
    public static final String CHECKOUT = "CHECKOUT";
    public static final String RETURN = "RETURN";

    private String type;
    private Location location;
    private String borrowerID;
    private String ownerID;
    private String bookID;
    private boolean bScan;
    private boolean oScan;

    /**
     * Creates a new Transaction object using default values for attributes.
     * <p>
     * Empty constructor needed for Firebase. Do not use in application code.
     */
    public Transaction() {
        type = CHECKOUT;
        bScan = false;
        oScan = false;
    }

    /**
     * Creates new annotation type that checks the value of an integer to see if it is equal to
     * either CHECKOUT or RETURN. CHECKOUT and RETURN constants used to simulate an enum. An actual
     * enum class is not necessary since enums use more memory than integer constants and we only
     * have two values that we care about.
     * <p>
     * See: https://android.jlelse.eu/android-performance-avoid-using-enum-on-android-326be0794dc3
     * https://stackoverflow.com/a/21874183/11039833
     */
    @StringDef({CHECKOUT, RETURN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /**
     * Creates a new Transaction object using specified values for attributes
     *
     * @param type     Determines type of transaction (ie. checkout or return)
     * @param location Location where transaction will occur
     * @param borrowerID String ID of user who is borrowing book
     * @param ownerID    String ID of user who owns the book
     * @param bookID   Identifier of book
     * @param bScan    Represents if the borrower has scanned the book
     * @param oScan    Represents if the owner has scanned the book
     */
    public Transaction(@Type String type, Location location, String borrowerID, String ownerID, String bookID, boolean bScan, boolean oScan) {
        this.type = type;
        this.location = location;
        this.borrowerID = borrowerID;
        this.ownerID = ownerID;
        this.bookID = bookID;
        this.bScan = bScan;
        this.oScan = oScan;
    }

    /**
     *
     * @return Type of transaction (ie. checkout or return)
     */
    @PropertyName("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type New type of transaction
     */
    public void setType(@Type String type){this.type = type;}

    /**
     * @return Location of the transaction
     */
    @PropertyName("location")
    public Location getLocation() {
        return location;
    }

    /**
     * @param location New location of transaction
     */
    @PropertyName("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Note: No setter provided since borrower can't change
     *
     * @return Borrower of the book associated with the transaction
     */
    @PropertyName("borrower")
    public String getBorrowerID() {
        return borrowerID;
    }

    public void setBorrowerID(String borrowerID){
        this.borrowerID = borrowerID;
    }

    /**
     * Note: No setter provided since owner change can't change
     *
     * @return Owner of the book associated with the transaction
     */
    @PropertyName("owner")
    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID){
        this.ownerID = ownerID;
    }

    /**
     * Note: No setter provided since bookID can't change
     *
     * @return ID of book involved in transaction
     */
    @PropertyName("bookID")
    public String getBookID() {
        return bookID;
    }

    /**
     * @return True if borrower has scanned the book, false otherwise
     */
    @Exclude
    @PropertyName("bScan")
    public boolean getBScan() {
        return bScan;
    }

    /**
     * @param bScan New value of bScan
     */
    @PropertyName("bScan")
    public void setBScan(boolean bScan) {
        this.bScan = bScan;
    }

    /**
     * @return True if owner has scanned the book, false otherwise
     */
    @Exclude
    @PropertyName("oScan")
    public boolean getOScan() {
        return oScan;
    }

    /**
     * @param oScan New value of oScan
     */
    @PropertyName("bScan")
    public void setOScan(boolean oScan) {
        this.oScan = oScan;
    }

    /**
     * @return True if the transaction is complete (both owner and borrower have scanned the book,
     * false otherwise
     */
    public boolean isComplete() {
        return oScan && bScan;
    }
}
