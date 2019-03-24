package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class NotificationsRefUtils extends RootRefUtils {
    public static final DatabaseReference NOTIFICATIONS_REF = ROOT_REF.child("notifications");
    public static final DatabaseReference PUSH_NOTIFICATIONS_REF = ROOT_REF.child("pushNotifications");

    public static final Query getNotificationsRef(String userID) {
        return NOTIFICATIONS_REF.child(userID)
                .orderByChild("creationDate/time");
    }

    public static final Query getNotificationsRef(User user) {
        return getNotificationsRef(user.getUserID());
    }

    public static final DatabaseReference getPushNotificationsRef(String userID) {
        return PUSH_NOTIFICATIONS_REF.child(userID);
    }

    public static final DatabaseReference getPushNotificationsRef(User user) {
        return PUSH_NOTIFICATIONS_REF.child(user.getUserID());
    }

}
