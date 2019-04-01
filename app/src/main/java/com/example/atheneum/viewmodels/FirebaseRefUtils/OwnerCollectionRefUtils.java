package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;

/**
 * The Owner collection ref utils.
 * utility class for getting firebase references to owner users
 */
public class OwnerCollectionRefUtils extends RootRefUtils {
    /**
     * The constant OWNER_COLLECTION_REF.
     */
    public static final DatabaseReference OWNER_COLLECTION_REF = ROOT_REF.child("ownerCollection");

    /**
     * Gets owner collection ref.
     *
     * @param user the user
     * @return the owner collection ref
     */
    public static DatabaseReference getOwnerCollectionRef(User user) {
        return OWNER_COLLECTION_REF.child(user.getUserID());
    }

    /**
     * Gets owner collection ref.
     *
     * @param userID the user id
     * @return the owner collection ref
     */
    public static DatabaseReference getOwnerCollectionRef(String userID) {
        return OWNER_COLLECTION_REF.child(userID);
    }

    /**
     * Gets owner book ref.
     *
     * @param user the user
     * @param book the book
     * @return the owner book ref
     */
    public static DatabaseReference getOwnerBookRef(User user, Book book) {
        return OWNER_COLLECTION_REF.child(user.getUserID()).child(book.getBookID());
    }
}
