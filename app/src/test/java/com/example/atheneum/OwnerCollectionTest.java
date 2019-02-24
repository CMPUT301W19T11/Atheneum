package com.example.atheneum;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.example.atheneum.Book.Status.ACCEPTED;
import static com.example.atheneum.Book.Status.AVAILABLE;
import static com.example.atheneum.Book.Status.BORROWED;
import static com.example.atheneum.Book.Status.REQUESTED;
import static org.junit.Assert.*;

public class OwnerCollectionTest {
    private User owner;
    private OwnerCollection ownerCollection;
    private  ArrayList<Book> ownList;
    private Book book1;
    private Book book2;
    private Book book3;
    private Book book4;
    private  Book book5;

    @Before
    public void init(){
        owner = new User();
        ownerCollection = new OwnerCollection(owner);
        book1 = new Book();
        book2 = new Book();
        book3 = new Book();
        book4 = new Book();
        book5 = new Book();
        ownList = new ArrayList<>();
    }

    @Test
    public void getUser(){
        assertEquals(owner, ownerCollection.getOwner());

    }

    @Test
    public void addBook(){
        ownList.add(book1);
        ownList.add(book2);
        ownList.add(book3);

        ownerCollection.addBook(book1);
        ownerCollection.addBook(book2);
        ownerCollection.addBook(book3);

        assertEquals(ownList, ownerCollection.getOwnList());
    }

    @Test
    public void deleteBook(){
        ownList.add(book4);
        ownerCollection.addBook(book4);

        ownList.remove(book4);
        ownerCollection.deleteBook(book4);

        assertEquals(ownList, ownerCollection.getOwnList());
    }

    @Test
    public void stausFilter(){
        ownerCollection.addBook(book4);
        ownerCollection.addBook(book5);

        book1.setStatus(REQUESTED);
        book2.setStatus(ACCEPTED);
        book3.setStatus(BORROWED);
        book4.setStatus(AVAILABLE);
        book5.setStatus(AVAILABLE);

        ownerCollection = new OwnerCollection(owner);

        ownerCollection.addBook(book1);
        ownerCollection.addBook(book2);
        ownerCollection.addBook(book3);
        ownerCollection.addBook(book4);
        ownerCollection.addBook(book5);


        ownList.clear();
        ownList.add(book4);
        ownList.add(book5);

        assertEquals(ownList,ownerCollection.filterBooks(AVAILABLE));

    }
}
