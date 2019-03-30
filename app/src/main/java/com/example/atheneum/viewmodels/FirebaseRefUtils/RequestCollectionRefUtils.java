package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.bumptech.glide.load.data.DataRewinder;
import com.google.firebase.database.DatabaseReference;

public class RequestCollectionRefUtils extends RootRefUtils {
    public static final DatabaseReference USER_REQUEST_COLLECTION_REF = ROOT_REF.child("requestCollection");
    public static final DatabaseReference BOOK_REQUEST_COLLECTION_REF = ROOT_REF.child("bookRequests");

    //returns DatabaseReference of all books that the specified user has made
    public static DatabaseReference getOwnerRequestCollectionRef(String userID) {
        return USER_REQUEST_COLLECTION_REF.child(userID);
    }

    //returns DatabaseReference of a specified book request that the  specified user has made
    public static DatabaseReference getSpecifiedOwnerRequest(String userID, String bookID){
        return USER_REQUEST_COLLECTION_REF.child(userID).child(bookID);
    }

    //return DatabaseReference of all users that made requests on specified book
    public static DatabaseReference getBookRequestCollectionRef(String bookID) {
        return BOOK_REQUEST_COLLECTION_REF.child(bookID);
    }


}