package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.google.firebase.database.DatabaseReference;

public class BooksRefUtils extends RootRefUtils {
    public static final DatabaseReference BOOKS_REF = ROOT_REF.child("books");

    public static DatabaseReference getBookRef(Book book) {
        return BOOKS_REF.child(book.getBookID());
    }
}
