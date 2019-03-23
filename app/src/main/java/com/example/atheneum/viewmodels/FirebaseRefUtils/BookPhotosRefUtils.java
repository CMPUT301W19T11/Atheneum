package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Photo;
import com.google.firebase.database.DatabaseReference;

/**
 * Generates database references for a photos attached to books.
 */
public class BookPhotosRefUtils extends RootRefUtils {
    public static final DatabaseReference BOOK_PHOTOS_REF = ROOT_REF.child("bookPhotos");

    /**
     * Get reference to all photos attached to a book.
     *
     * @param bookID Unique identifier for the book.
     * @return Reference to a node containing all the photos for the book.
     */
    public static DatabaseReference getBookPhotosRef(String bookID) {
        return BOOK_PHOTOS_REF.child(bookID);
    }

    /**
     * Get reference to all photos attached to a book.
     *
     * @param book Book to add/retrieve/delete pictures for.
     * @return Reference to a node containing all the photos for the book.
     */
    public static DatabaseReference getBookPhotosRef(Book book) {
        return BOOK_PHOTOS_REF.child(book.getBookID());
    }

    /**
     * Get reference to particular photo attached to a book.
     *
     * @param bookID Unique identifier for the book.
     * @param photoID Unique identifier for the photo.
     * @return Reference to a node containing the photo.
     */
    public static DatabaseReference getBookPhotoRef(String bookID, String photoID) {
        return BOOK_PHOTOS_REF.child(bookID).child(photoID);
    }

    /**
     * Get reference to particular photo attached to a book.
     *
     * @param book Book the photo is attached to.
     * @param photo Photo that we are interested in.
     * @return Reference to a node containing the photo.
     */
    public static DatabaseReference getBookPhotoRef(Book book, Photo photo) {
        return BOOK_PHOTOS_REF.child(book.getBookID()).child(photo.getPhotoID());
    }
}
