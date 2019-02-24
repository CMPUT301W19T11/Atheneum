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
    private ArrayList<String> testListPhotos;

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
