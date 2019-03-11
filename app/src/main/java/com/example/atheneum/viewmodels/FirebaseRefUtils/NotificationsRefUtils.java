package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;

public class NotificationsRefUtils extends RootRefUtils {
    public static final DatabaseReference NOTIFICATIONS_REF = ROOT_REF.child("notifications");

    public static final DatabaseReference getNotificationsRef(String userID) {
        return NOTIFICATIONS_REF.child(userID);
    }

    public static final DatabaseReference getNotificationsRef(User user) {
        return NOTIFICATIONS_REF.child(user.getUserID());
    }
}
