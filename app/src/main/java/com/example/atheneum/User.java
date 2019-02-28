/*
 * Copyright <2019> <CMPUT301W19T11>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum;

import android.app.Notification;

import java.util.ArrayList;

public class User {
    private String userID = "";
    private String userName = "";
    private String phoneNumber = "";
    private double ownerRate = 0;
    private double borrowerRate = 0;
    private ArrayList<String> photos = new ArrayList<String>();

    /**
     * init user class with blank constructor
     *
     * Note: Needed for Firebase, use the other constructors in application code.
     */
    public User(){
    }

    /**
     * init user class with only username and userID, all other values are default
     *
     * @param userID
     * @param userName
     */
    public User(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    /**
     * init user class with specified attributes
     * @param userID
     * @param userName
     * @param phoneNumber
     * @param ownerRate
     * @param borrowerRate
     * @param userID
     */
    public User(String userID, String userName, String phoneNumber, double ownerRate, double borrowerRate) {
        this.userID = userID;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.ownerRate = ownerRate;
        this.borrowerRate = borrowerRate;
    }

    /**
     * Note: No setter provided since can't change userID after construction, provided unique ID by
     *       Firebase Auth
     *
     * @return UUID of the user
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Changes the user name of the user
     *
     * @param userName New username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return user name of class
     */
    public String getUserName(){return this.userName;}

    /**
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    /**
     *
     * @return phone number of the user
     */
    public String getPhoneNumber(){return this.phoneNumber;}

    /**
     *
     * @param rate
     */
    public void setOwnerRate(double rate){this.ownerRate = rate;}

    /**
     *
     * @return owner rating of the user
     */
    public double getOwnerRate(){return this.ownerRate;}

    /**
     *
     * @param rate
     */
    public void setBorrowerRate(double rate){this.borrowerRate = rate;}

    /**
     *
     * @return borrower rating of the user
     */
    public double getBorrowerRate(){return  this.borrowerRate;}

    /**
     *
     * @param photos
     */
    public void setPhotos(ArrayList<String> photos){this.photos = photos;}

    /**
     *
     * @return collection of the photos of books
     */
    public ArrayList<String> getPhotos(){return this.photos;}

    /**
     *
     * @param photo
     */
    public void addPhotos(String photo){this.photos.add(photo);}


    /**
     *
     * @param photo
     */
    public void deletePhotos(String photo){this.photos.remove(photo);}
}
