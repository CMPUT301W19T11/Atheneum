package com.example.atheneum;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class OwnerCollectionTest {
    private User owner;
    private OwnerCollection ownerCollection;
    private ArrayList<Book> testBookList;

    @Before
    public void init() {
        owner = new User();
        ownerCollection = new OwnerCollection(owner);
        testBookList= new ArrayList<Book>();

        // Add books to the test list
        testBookList.add(new Book());
        testBookList.add(new Book(1234567890, "Title", "description", "author", owner, null, Book.Status.AVAILABLE));
        testBookList.add(new Book(54321, "Unavail", "description", "author", owner, null, Book.Status.BORROWED));
        testBookList.add(new Book(43134, "Requested", "description", "author", owner, null, Book.Status.REQUESTED));
        testBookList.add(new Book(453633134, "Accepted", "description", "author", owner, null, Book.Status.ACCEPTED));
        // 0 is default constructor, 1 is available, 2, is borrowed, 3 is requested, 4 is accepted
    }

    @Test
    public void verifyOwner(){
        assertTrue(ownerCollection.getOwner().equals(owner));
    }

    @Test
    public void setOwnerTest(){
        assertTrue(ownerCollection.getOwner().equals(owner));
        User newOwner = new User();
        ownerCollection.setOwner(newOwner);
        assertFalse(ownerCollection.getOwner().equals(owner));
        assertTrue(ownerCollection.getOwner().equals(newOwner));
    }

    @Test
    public void addTest(){
        for (Book b : testBookList) {
            ownerCollection.addBook(b);
            assertTrue(ownerCollection.getOwnList().contains(b));
        }
    }

    @Test
    public void deleteTest(){
        for (Book b : testBookList) {
            ownerCollection.addBook(b);
            assertTrue(ownerCollection.getOwnList().contains(b));
        }

        for (Book b : testBookList) {
            ownerCollection.deleteBook(b);
            assertFalse(ownerCollection.getOwnList().contains(b));
        }

        assertTrue(ownerCollection.getOwnList().isEmpty());
    }

    @Test
    public void getListTest(){
        for (Book b : testBookList) {
            ownerCollection.addBook(b);
        }

        assertTrue(ownerCollection.getOwnList().equals(testBookList));
    }

    @Test
    public void setListTest(){
        ownerCollection.setOwnList(testBookList);

        assertTrue(ownerCollection.getOwnList().equals(testBookList));
    }

    @Test
    public void constructorListTest(){
        OwnerCollection testList = new OwnerCollection(owner, testBookList);

        assertTrue(testList.getOwner().equals(owner));
        assertTrue(testList.getOwnList().equals(testBookList));
    }

    @Test
    public void filterTest() {
        Book.Status[] statuses = new Book.Status[]{Book.Status.AVAILABLE,
                Book.Status.ACCEPTED,
                Book.Status.REQUESTED,
                Book.Status.BORROWED};

        for (Book.Status s : statuses) {
            ArrayList<Book> filteredBooks = ownerCollection.filterBooks(s);

            assertTrue(filteredBooks.size() > 0);

            for (Book b : filteredBooks) {
                assertTrue(b.getStatus().equals(s));
            }

        }
    }
}

