/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
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

    private String userName;
    private String phoneNumber;
    private double ownerRate;
    private double borrowerRate;
    private ArrayList<String> owning;
    private ArrayList<String> borrowing;
    private ArrayList<String> photos;

    public User(String userName, String phoneNumber, double ownerRate, double borrowerRate){
        this.userName = userName;
        owning = new ArrayList<String>();
        borrowing = new ArrayList<String>();
        photos = new ArrayList<String>();
        this.ownerRate = ownerRate;
        this.borrowerRate = borrowerRate;
        this.phoneNumber = phoneNumber;
    }

    public String getUserName(){return this.userName;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getPhoneNumber(){return this.phoneNumber;}

    public void setOwnerRate(double rate){this.ownerRate = rate;}

    public double getOwnerRate(){return this.ownerRate;}

    public void setBorrowerRate(double rate){this.borrowerRate = rate;}

    public double getBorrowerRate(){return  this.borrowerRate;}

    public void setOwning(ArrayList<String> owning){this.owning = owning;}

    public ArrayList<String> getOwning() { return this.owning; }

    public void setBorrowing(ArrayList<String> borrowing){this.borrowing = borrowing;}

    public ArrayList<String> getBorrowing(){ return  this.borrowing; }

    public void setPhotos(ArrayList<String> photos){this.photos = photos;}

    public ArrayList<String> getPhotos(){return this.photos;}

    public void addOwning(String bookName){this.owning.add(bookName);}

    public void addBorrowing(String bookName){this.borrowing.add(bookName);}

    public void addPhotos(String photo){this.photos.add(photo);}

    public void deleteOwing(String bookName){this.owning.remove(bookName);}

    public void deleteBorrowing(String bookName){this.borrowing.remove(bookName);}

    public void deletePhotos(String photo){this.photos.remove(photo);}



}
