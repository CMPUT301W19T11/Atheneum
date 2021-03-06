package com.example.atheneum;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void GetIsbn() {
        Book book = new Book();
        long bi = 1234L;

        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = 1234567890123L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = 9999999999999L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = -1L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);
    }

    @Test
    public void GetTitle() {
        Book book = new Book();
        String bt = "the seven seas";

        book.setTitle(bt);
        assertEquals(book.getTitle(), bt);

        bt = "";
        book.setTitle(bt);
        assertEquals(book.getTitle(), bt);

        bt = "3q984r98asdhfkjwoiruiofhlksjrlkjioi142ro8ewofhjlksdah" +
                "fjksdhjflkhsdkjfhasfhsjkfhkjsadhfjksajfhsadjfhkjsd" +
                "hfjkdsafjkhdsafasjdkfhjkhsadjlkfjlksfjhlkjlkfjlksa";
        book.setTitle(bt);
        assertEquals(book.getTitle(), bt);
    }

    @Test
    public void GetDescription() {
        Book book = new Book();
        String bd = "sail the 7 seas matey!!!";

        book.setDescription(bd);
        assertEquals(book.getDescription(), bd);

        bd = "";
        book.setDescription(bd);
        assertEquals(book.getDescription(), bd);

        bd = "ajsoir8o34o8fjoisadjlkfjsadlkfjlkasdjflksjadlkfjlkdsf" +
                "sjasldfl;ls;fk;saf;lksa;lfk;slafhhuoishfjn48fjdkf2" +
                "hKY UR9YUYysi*YYIUHLLhkLHHIUOIUOIEY98T3O   UGH9852";
        book.setDescription(bd);
        assertEquals(book.getDescription(), bd);
    }

    @Test
    public void GetAuthor() {
        Book book = new Book();
        String ba = "sailor man";

        book.setAuthor(ba);
        assertEquals(book.getAuthor(), ba);

        ba = "";
        book.setAuthor(ba);
        assertEquals(book.getAuthor(), ba);

        ba = "ajthemanwhoiscrazybutalsoisrationalandsksjadlkfjlkdsf" +
                "sjasldfl;ls;fk;saf;lksa;lfk;slafhhuoishfjn48fjdkf2" +
                "hKY UR9YUYysi*YYIUHLLhkLHHIUOIUOIEY98T3O   UGH9852";
        book.setAuthor(ba);
        assertEquals(book.getAuthor(), ba);
    }

    @Test
    public void GetOwner() {
        Book book = new Book();
        User owner = new User();

        book.setOwnerID(owner.getUserID());
        assertThat(book.getOwnerID(), is(owner.getUserID()));
    }

    @Test
    public void GetBorrower() {
        Book book = new Book();
        User borrower = new User();

        book.setBorrowerID(borrower.getUserID());
        assertThat(book.getBorrowerID(), is(borrower.getUserID()));
    }

    @Test
    public void GetStatus() {
        Book book = new Book();

        book.setStatus(Book.Status.AVAILABLE);
        assertEquals(book.getStatus(), Book.Status.AVAILABLE);

        book.setStatus(Book.Status.REQUESTED);
        assertEquals(book.getStatus(), Book.Status.REQUESTED);

        book.setStatus(Book.Status.ACCEPTED);
        assertEquals(book.getStatus(), Book.Status.ACCEPTED);

        book.setStatus(Book.Status.BORROWED);
        assertEquals(book.getStatus(), Book.Status.BORROWED);
    }
}