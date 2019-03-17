package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class UsersRefUtils extends RootRefUtils {
    public static final DatabaseReference USERS_REF = ROOT_REF.child("users");

    public static final DatabaseReference getUsersRef(User user) {
        return USERS_REF.child(user.getUserID());
    }

    public static final DatabaseReference getUsersRef(String userID) {
        return USERS_REF.child(userID);
    }

    public static final Query getUserNameQuery(String partialUserName) {
        return USERS_REF.orderByChild("userName").startAt(partialUserName).endAt(partialUserName + "\uf8ff");
    }
}
