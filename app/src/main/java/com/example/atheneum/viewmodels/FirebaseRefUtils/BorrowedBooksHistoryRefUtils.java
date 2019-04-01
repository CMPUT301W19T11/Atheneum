package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * The Borrowed books history ref utils.
 * utility class for keeping history of borrowed books for users
 * to get recommendations
 */
public class BorrowedBooksHistoryRefUtils {
    /**
     * The constant BORROWED_BOOKS_HISTORY_REF.
     */
    public static final DatabaseReference BORROWED_BOOKS_HISTORY_REF = RootRefUtils.ROOT_REF.child("borrowedBooksHistory");

    /**
     * Gets user histories containing isbn.
     *
     * @param isbn the isbn
     * @return the user histories containing isbn
     */
    public static final Query getUserHistoriesContainingIsbn(Long isbn) {
        return BORROWED_BOOKS_HISTORY_REF.orderByChild(Long.toString(isbn)).equalTo(true);
    }
}
