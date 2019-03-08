/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.atheneum.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UserDeviceTokenRefUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


/**
 * Firebase messaging service used for push notifications
 * Mainly used for receiving requests
 *
 * See: https://firebase.google.com/docs/cloud-messaging/android/client
 * See: https://www.codementor.io/flame3/send-push-notifications-to-android-with-firebase-du10860kb
 * See: https://firebase.google.com/docs/cloud-messaging/android/first-message
 * See: https://github.com/firebase/quickstart-android/blob/f2888b0a41376f474952755b1f93e079682ffa54/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.java#L47-L90
 */
public class RequestFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = RequestFirebaseMessagingService.class.getSimpleName();

    /**
     * Called when message is received.
     * Will execute when application is in foreground
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // force send push notification when application is in the foreground
        sendNotification(remoteMessage.getNotification().getBody());
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *
     * @param token The new token
     */
    @Override
    public void onNewToken(String token) {
        //send new token to firebase
        sendRegistrationToServer(token);
    }

    /**
     * Persist FCM registration token to firebase database
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        final FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
        if (firebaseUser != null) {
            final DatabaseReference ref = UserDeviceTokenRefUtils
                    .getUserDeviceTokensRef(firebaseUser.getUid());
            ref.setValue(token);
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * Called when application is in the background
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.profile_title);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_book_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
