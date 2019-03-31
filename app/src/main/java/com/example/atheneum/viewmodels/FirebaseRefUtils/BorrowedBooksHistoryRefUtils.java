package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class BorrowedBooksHistoryRefUtils {
    public static final DatabaseReference BORROWED_BOOKS_HISTORY_REF = RootRefUtils.ROOT_REF.child("borrowedBooksHistory");

    public static final Query getUserHistoriesContainingIsbn(Long isbn) {
        return BORROWED_BOOKS_HISTORY_REF.orderByChild(Long.toString(isbn)).equalTo(true);
    }
}
