package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.bumptech.glide.load.data.DataRewinder;
import com.google.firebase.database.DatabaseReference;

/**
 * The type Request collection ref utils.
 * utility class for getting firebase references to requests
 */
public class RequestCollectionRefUtils extends RootRefUtils {
    /**
     * The constant USER_REQUEST_COLLECTION_REF.
     */
    public static final DatabaseReference USER_REQUEST_COLLECTION_REF = ROOT_REF.child("requestCollection");
    /**
     * The constant BOOK_REQUEST_COLLECTION_REF.
     */
    public static final DatabaseReference BOOK_REQUEST_COLLECTION_REF = ROOT_REF.child("bookRequests");
    /**
     * The constant BOOK_REQUEST_BOOK_REF.
     */
    public static final DatabaseReference BOOK_REQUEST_BOOK_REF = ROOT_REF.child("books");

    /**
     * Gets owner request collection ref.
     *
     * @param userID the user id
     * @return the owner request collection ref
     */
//returns DatabaseReference of all books that the specified user has made
    public static DatabaseReference getOwnerRequestCollectionRef(String userID) {
        return USER_REQUEST_COLLECTION_REF.child(userID);
    }

    /**
     * Get specified owner request database reference.
     *
     * @param userID the user id
     * @param bookID the book id
     * @return the database reference
     */
//returns DatabaseReference of a specified book request that the  specified user has made
    public static DatabaseReference getSpecifiedOwnerRequest(String userID, String bookID){
        return USER_REQUEST_COLLECTION_REF.child(userID).child(bookID);
    }

    /**
     * Gets book request collection ref.
     *
     * @param bookID the book id
     * @return the book request collection ref
     */
//return DatabaseReference of all users that made requests on specified book
    public static DatabaseReference getBookRequestCollectionRef(String bookID) {
        return BOOK_REQUEST_COLLECTION_REF.child(bookID);
    }

    /**
     * Get requested bookr ef database reference.
     *
     * @param bookid the bookid
     * @return the database reference
     */
    public static DatabaseReference getRequestedBookrEF(String bookid){
        return BOOK_REQUEST_BOOK_REF.child(bookid);
    }


}