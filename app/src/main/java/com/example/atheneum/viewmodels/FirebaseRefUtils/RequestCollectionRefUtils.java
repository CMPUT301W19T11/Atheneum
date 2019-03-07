package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;

public class RequestCollectionRefUtils extends RootRefUtils {
    public static final DatabaseReference OWNER_REQUEST_COLLECTION_REF = ROOT_REF.child("requestCollection");
    public static final DatabaseReference BOOK_REQUEST_COLLECTION_REF = ROOT_REF.child("bookRequests");
}
