package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * The Notifications ref util
 * utility class for getting firebase references to notifications
 */
public class NotificationsRefUtils extends RootRefUtils {
    /**
     * The constant NOTIFICATIONS_REF.
     */
    public static final DatabaseReference NOTIFICATIONS_REF = ROOT_REF.child("notifications");
    /**
     * The constant PUSH_NOTIFICATIONS_REF.
     */
    public static final DatabaseReference PUSH_NOTIFICATIONS_REF = ROOT_REF.child("pushNotifications");

    /**
     * Gets notifications ref.
     *
     * @param userID the user id
     * @return the notifications ref
     */
    public static final Query getNotificationsRef(String userID) {
        return NOTIFICATIONS_REF.child(userID)
                .orderByChild("creationDate/time");
    }

    /**
     * Gets notifications ref.
     *
     * @param user the user
     * @return the notifications ref
     */
    public static final Query getNotificationsRef(User user) {
        return getNotificationsRef(user.getUserID());
    }

    /**
     * Gets push notifications ref.
     *
     * @param userID the user id
     * @return the push notifications ref
     */
    public static final DatabaseReference getPushNotificationsRef(String userID) {
        return PUSH_NOTIFICATIONS_REF.child(userID);
    }

    /**
     * Gets push notifications ref.
     *
     * @param user the user
     * @return the push notifications ref
     */
    public static final DatabaseReference getPushNotificationsRef(User user) {
        return PUSH_NOTIFICATIONS_REF.child(user.getUserID());
    }

}
