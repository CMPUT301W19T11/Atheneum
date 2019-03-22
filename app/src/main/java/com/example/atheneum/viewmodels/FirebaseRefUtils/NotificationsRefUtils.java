package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;

public class NotificationsRefUtils extends RootRefUtils {
    public static final DatabaseReference NOTIFICATIONS_REF = ROOT_REF.child("notifications");
    public static final DatabaseReference PUSH_NOTIFICATIONS_REF = ROOT_REF.child("pushNotifications");

    public static final DatabaseReference getNotificationsRef(String userID) {
        return NOTIFICATIONS_REF.child(userID);
    }

    public static final DatabaseReference getNotificationsRef(User user) {
        return NOTIFICATIONS_REF.child(user.getUserID());
    }

    public static final DatabaseReference getPushNotificationsRef(String userID) {
        return PUSH_NOTIFICATIONS_REF.child(userID);
    }

    public static final DatabaseReference getPushNotificationsRef(User user) {
        return PUSH_NOTIFICATIONS_REF.child(user.getUserID());
    }

}
