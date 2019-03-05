package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;

public class OwnerCollectionRefUtils extends RootRefUtils {
    public static final DatabaseReference OWNER_COLLECTION_REF = ROOT_REF.child("ownerCollection");

    public static DatabaseReference getOwnerCollectionRef(User user) {
        return OWNER_COLLECTION_REF.child(user.getUserID());
    }

    public static DatabaseReference getOwnerCollectionRef(String userID) {
        return OWNER_COLLECTION_REF.child(userID);
    }

    public static DatabaseReference getOwnerBookRef(User user, Book book) {
        return OWNER_COLLECTION_REF.child(user.getUserID()).child(book.getBookID());
    }
}
