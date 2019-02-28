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

import com.example.atheneum.models.Book;
import com.example.atheneum.models.BorrowerCollection;
import com.example.atheneum.models.User;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class BorrowerCollectionTest {
    private User borrower;
    private BorrowerCollection bCollection;

    @Before
    public void setup(){
        borrower = new User();
        bCollection = new BorrowerCollection(borrower);

    }

    @Test
    public void GetBorrower(){assertThat(borrower, is(bCollection.getBorrower()));}

    @Test
    public void SetBorrower(){
        User newUser = new User();
        bCollection.setBorrower(newUser);
        assertThat(newUser, is(bCollection.getBorrower()));
    }

    @Test
    public void GetSetBorrowList(){
        BorrowerCollection collection = new BorrowerCollection();
        ArrayList<Book> borrowList = new ArrayList<>();

        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();
        borrowList.add(book1);
        borrowList.add(book2);
        borrowList.add(book3);

        collection.setBorrowList(borrowList);
        ArrayList<Book> returnedCollection = collection.getBorrowList();

        assertTrue(returnedCollection.contains(book1));
        assertTrue(returnedCollection.contains(book2));
        assertTrue(returnedCollection.contains(book3));

        assertEquals(returnedCollection.indexOf(book1), 0);
        assertEquals(returnedCollection.indexOf(book2), 1);
        assertEquals(returnedCollection.indexOf(book3), 2);

        assertEquals(returnedCollection.size(), 3);
    }

    @Test
    public void AddBook(){
        BorrowerCollection collection = new BorrowerCollection();

        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();

        collection.addBook(book1);
        collection.addBook(book2);
        collection.addBook(book3);

        ArrayList<Book> returnedCollection = collection.getBorrowList();

        assertTrue(returnedCollection.contains(book1));
        assertTrue(returnedCollection.contains(book2));
        assertTrue(returnedCollection.contains(book3));


        assertEquals(returnedCollection.indexOf(book1), 0);
        assertEquals(returnedCollection.indexOf(book2), 1);
        assertEquals(returnedCollection.indexOf(book3), 2);

        assertEquals(returnedCollection.size(), 3);


    }



}
