package com.example.atheneum.utils;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Simple class that holds references to major tables in our database. Can be used to compose queries.
 */
public class DatabaseRefUtils {
    private static final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference USERS_REF = rootRef.child("users");
    public static final DatabaseReference OWNER_COLLECTION_REF = rootRef.child("ownerCollection");
    public static final DatabaseReference REQUEST_COLLECTION_REF = rootRef.child("requestCollection");

}
