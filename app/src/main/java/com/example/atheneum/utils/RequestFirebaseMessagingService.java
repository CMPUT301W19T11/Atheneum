package com.example.atheneum.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Firebase messaging service used for push notifications
 * Mainly used for requests
 *
 * See: https://firebase.google.com/docs/cloud-messaging/android/client
 * See: https://www.codementor.io/flame3/send-push-notifications-to-android-with-firebase-du10860kb
 */
public class RequestFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
}
