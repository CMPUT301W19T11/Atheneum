package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;

public class UsersRefUtils extends RootRefUtils {
    public static final DatabaseReference USERS_REF = ROOT_REF.child("users");

    public static final DatabaseReference getUsersRef(User user) {
        return USERS_REF.child(user.getUserID());
    }

    public static final DatabaseReference getUsersRef(String userID) {
        return USERS_REF.child(userID);
    }
}
