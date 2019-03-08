package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;

public class DatabaseQueryHelper {
    private static final String TAG = DatabaseWriteHelper.class.getSimpleName();

    public static void getUserFromUserID(String userID) {
        DatabaseReference ref = UsersRefUtils.getUsersRef(userID);

        //TODO: notes: a little hard to implement because it would rely on callbacks
    }
}
