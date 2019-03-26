package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class BooksRefUtils extends RootRefUtils {
    public static final DatabaseReference BOOKS_REF = ROOT_REF.child("books");

    public static DatabaseReference getBookRef(Book book) {
        return BOOKS_REF.child(book.getBookID());
    }

    public static DatabaseReference getBookRef(String bookID) {
        return BOOKS_REF.child(bookID);
    }

    public static Query getOwnerBooksRef(String ownerID) {
        return BOOKS_REF.orderByChild("ownerID").equalTo(ownerID);
    }
}
