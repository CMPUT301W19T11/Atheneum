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

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class UserTest {
    private User user1;
    private OwnerCollection testListOwning;
    private BorrowerCollection testListBorrowing;
    private ArrayList<String> testListPhotos;
    private Book deletedBook;

    @Before
    public void init(){
        user1 = new User("user1@ualberta.ca", "123-456-7899", 0, 0);
        testListPhotos = new ArrayList<String>();
    }

    @Test
    public void checkUserName(){
        assertEquals("user1@ualberta.ca", user1.getUserName());
    }

    @Test
    public void getPhoneNumber(){
        String phoneNumber = "987-654-3211";
        user1.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user1.getPhoneNumber());
    }

    @Test
    public void getOwnerRate(){
        double rate = 10.0;
        double delta = 0.000000001;
        user1.setOwnerRate(rate);
        assertEquals(rate, user1.getOwnerRate(), delta);
    }

    @Test
    public void getBorrowerRate(){
        double rate = 10.0;
        double delta = 0.000000001;
        user1.setBorrowerRate(rate);
        assertEquals(rate, user1.getBorrowerRate(), delta);
    }


    @Test
    public void getPhotos(){

        testListPhotos.add("photo1");
        testListPhotos.add("photo2");
        testListPhotos.add("photo3");

        user1.setPhotos(testListPhotos);
        assertTrue(testListPhotos.equals(user1.getPhotos()));
    }

    @Test
    public void addOwning(){
        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();
        user1.addOwning(book1);
        user1.addOwning(book2);
        user1.addOwning(book3);
        testListOwning = user1.getOwning();
        Book book4 = new Book();
        testListOwning.addBook(book4);
        user1.addOwning(book4);

        assertTrue(testListOwning.equals(user1.getOwning()));
    }

    @Test
    public void deleteOwning(){
        deletedBook = new Book();
        user1.addOwning(deletedBook);
        testListOwning = user1.getOwning();
        testListOwning.addBook(deletedBook);
        testListOwning.deleteBook(deletedBook);
        user1.deleteOwing(deletedBook);

//        assertTrue(testListOwning.equals(user1.getOwning()));
        assertEquals(testListOwning, user1.getOwning());
    }

    @Test
    public void addBorrowing(){
        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();
        user1.addBorrowing(book1);
        user1.addBorrowing(book2);
        user1.addBorrowing(book3);
        Book book4 = new Book();
        testListBorrowing = user1.getBorrowing();
        testListBorrowing.addBook(book4);
        user1.addBorrowing(book4);

        assertTrue(testListBorrowing.equals(user1.getBorrowing()));
    }

    @Test
    public void addPhoto(){
        String newPhoto = "photo4";
        testListPhotos.add(newPhoto);
        user1.addPhotos(newPhoto);

        assertTrue(testListPhotos.equals(user1.getPhotos()));
    }

    @Test
    public void deletePhotos(){
        String newPhoto = "photo4";
        testListPhotos.remove(newPhoto);
        user1.deletePhotos(newPhoto);

        assertTrue(testListPhotos.equals(user1.getPhotos()));
    }



}
