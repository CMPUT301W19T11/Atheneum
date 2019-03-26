package com.example.atheneum.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.activities.ShowRequestInfoActivity;
import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.NotificationIntentProvider;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.NotificationsRefUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * A service to run indefinitely for push notifications
 * See: https://developer.android.com/guide/components/services
 */
public class PushNotificationsService extends Service {
    public static DatabaseReference pushNotificationsRef = null;
    public static ChildEventListener pushNotificationsListener = null;

    private int pushNotificationID = 0;

    private final static String TAG = PushNotificationsService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start listening for notifications in DB
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "starting");
        //notifications
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()
                && pushNotificationsRef == null
                && pushNotificationsListener == null) {
            Log.i(TAG, "starting listener");
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            pushNotificationsRef = NotificationsRefUtils
                    .getPushNotificationsRef(firebaseUser.getUid());
            pushNotificationsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    sendNotification(notification);
                    DatabaseWriteHelper.deletePushNotification(notification);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            pushNotificationsRef.addChildEventListener(pushNotificationsListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i(TAG, "stopping");
        return super.stopService(name);
    }

    /**
     * Stop listening for notifications in DB
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "destroying");
        pushNotificationsRef.removeEventListener(pushNotificationsListener);
        pushNotificationsRef = null;
        pushNotificationsListener = null;
        super.onDestroy();
    }

    /**
     * Construct and send notification to app. user's phone
     * See: https://developer.android.com/guide/topics/ui/notifiers/notifications
     * See: https://developer.android.com/training/notify-user/build-notification
     * See: https://developer.android.com/training/notify-user/expanded
     * See: https://developer.android.com/training/notify-user/navigation#java
     *
     * @param notification
     */
    private void sendNotification(Notification notification) {

        Intent notifyIntent = NotificationIntentProvider
                .obtainIntent(this.getApplicationContext(), notification);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notifyIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        String channelId = getString(R.string.profile_title);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getBaseContext(), channelId)
                        .setSmallIcon(R.drawable.ic_book_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(notification.getMessage())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.getMessage()))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(notifyPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(pushNotificationID, notificationBuilder.build());
        pushNotificationID++;
    }
}
