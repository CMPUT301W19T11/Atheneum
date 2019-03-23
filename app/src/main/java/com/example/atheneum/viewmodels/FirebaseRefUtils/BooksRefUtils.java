package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.google.firebase.database.DatabaseReference;

/**
 * Class used to generate References to objects under the books node in the database.
 *
 * Used to prevent leaking database internal knowledge to the views (activities/fragments) if possible.
 */
public class BooksRefUtils extends RootRefUtils {
    public static final DatabaseReference BOOKS_REF = ROOT_REF.child("books");

    /**
     * @param book Book object we're interested in
     * @return Reference to book object in database
     */
    public static DatabaseReference getBookRef(Book book) {
        return BOOKS_REF.child(book.getBookID());
    }

    /**
     * @param bookID Book ID of book we're interested in
     * @return Reference to book object in database
     */
    public static DatabaseReference getBookRef(String bookID) {
        return BOOKS_REF.child(bookID);
    }

    /**
     * @param bookID Book ID of book we're interested in
     * @return Reference to list of photos associated with the bookID
     */
    public static DatabaseReference getBookPhotosRef(String bookID) {
        return getBookRef(bookID).child("photos");
    }
}
