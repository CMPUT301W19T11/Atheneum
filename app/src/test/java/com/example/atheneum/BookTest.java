package com.example.atheneum;

import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void GetIsbn() {
        Book book = new Book();
        Long bi = Long.valueOf(1234);

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

        book.setOwner(owner);
        assertEquals(book.getOwner(), owner);

        book.setOwner(null);
        assertNull(book.getOwner());
    }

    @Test
    public void GetBorrower() {
        Book book = new Book();
        User borrower = new User();

        book.setBorrower(borrower);
        assertEquals(book.getBorrower(), borrower);

        book.setBorrower(null);
        assertNull(book.getBorrower());
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

    @Test
    public void GetRequests() {
        Book book = new Book();

        ArrayList<Request> requests = new ArrayList<Request>();
        Request r1 = new Request();
        Request r2 = new Request();
        Request r3 = new Request();
        requests.add(r1);
        requests.add(r2);
        requests.add(r3);

        book.setRequests(requests);
        ArrayList<Request> returnedRequests = book.getRequests();

        assertTrue(returnedRequests.contains(r1));
        assertTrue(returnedRequests.contains(r2));
        assertTrue(returnedRequests.contains(r3));

        assertEquals(returnedRequests.indexOf(r1), 0);
        assertEquals(returnedRequests.indexOf(r2), 1);
        assertEquals(returnedRequests.indexOf(r3), 2);

        assertEquals(returnedRequests.size(), 3);
    }

    @Test
    public void AddRequest() {
        Book book = new Book();

        Request r1 = new Request();
        Request r2 = new Request();
        Request r3 = new Request();

        book.addRequest(r1);
        book.addRequest(r2);
        book.addRequest(r3);

        ArrayList<Request> returnedRequests = book.getRequests();

        assertTrue(returnedRequests.contains(r1));
        assertTrue(returnedRequests.contains(r2));
        assertTrue(returnedRequests.contains(r3));

        assertEquals(returnedRequests.indexOf(r1), 0);
        assertEquals(returnedRequests.indexOf(r2), 1);
        assertEquals(returnedRequests.indexOf(r3), 2);

        assertEquals(returnedRequests.size(), 3);
    }

    @Test
    public void GetPhotos() {
        Book book = new Book();

        ArrayList<String> photos = new ArrayList<String>();
        String p1 = "219804uro8dswf";
        String p2 = "324rsdfsasssss";
        String p3 = "434rdfsdfdsfdsfs";
        photos.add(p1);
        photos.add(p2);
        photos.add(p3);

        book.setPhotos(photos);
        ArrayList<String> returnedPhotos = book.getPhotos();

        assertTrue(returnedPhotos.contains(p1));
        assertTrue(returnedPhotos.contains(p2));
        assertTrue(returnedPhotos.contains(p3));

        assertEquals(returnedPhotos.indexOf(p1), 0);
        assertEquals(returnedPhotos.indexOf(p2), 1);
        assertEquals(returnedPhotos.indexOf(p3), 2);

        assertEquals(returnedPhotos.size(), 3);
    }

    @Test
    public void AddPhoto() {
        Book book = new Book();

        String p1 = "219804uro8dswf";
        String p2 = "324rsdfsasssss";
        String p3 = "434rdfsdfdsfdsfs";
        book.addPhoto(p1);
        book.addPhoto(p2);
        book.addPhoto(p3);

        ArrayList<String> returnedPhotos = book.getPhotos();

        assertTrue(returnedPhotos.contains(p1));
        assertTrue(returnedPhotos.contains(p2));
        assertTrue(returnedPhotos.contains(p3));

        assertEquals(returnedPhotos.indexOf(p1), 0);
        assertEquals(returnedPhotos.indexOf(p2), 1);
        assertEquals(returnedPhotos.indexOf(p3), 2);

        assertEquals(returnedPhotos.size(), 3);
    }

    public void DeletePhotos() {
        Book book = new Book();

        String p1 = "219804uro8dswf";
        String p2 = "324rsdfsasssss";
        String p3 = "434rdfsdfdsfdsfs";
        book.addPhoto(p1);
        book.addPhoto(p2);
        book.addPhoto(p3);

        book.deletePhoto(p2);

        ArrayList<String> returnedPhotos = book.getPhotos();
        assertFalse(returnedPhotos.contains(p2));
        assertEquals(returnedPhotos.size(), 2);

        book.deletePhotos();
        returnedPhotos = book.getPhotos();
        assertEquals(returnedPhotos.size(), 0);
        assertFalse(returnedPhotos.contains(p1));
        assertFalse(returnedPhotos.contains(p2));
        assertFalse(returnedPhotos.contains(p3));
    }
}