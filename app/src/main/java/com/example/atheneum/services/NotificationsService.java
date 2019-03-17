package com.example.atheneum.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.NotificationsRefUtils;
import com.example.atheneum.viewmodels.UserNotificationsViewModel;
import com.example.atheneum.viewmodels.UserNotificationsViewModelFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * A service to run indefinitely for push notifications
 * See: https://developer.android.com/guide/components/services
 */
public class NotificationsService extends Service {
    public static DatabaseReference notificationsRef = null;
    public static ChildEventListener notificationListener = null;

    private int pushNotificationID = 0;

    private final static String TAG = NotificationsService.class.getSimpleName();

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
                && notificationsRef == null
                && notificationListener == null) {
            Log.i(TAG, "starting listener");
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            notificationsRef = NotificationsRefUtils
                    .getNotificationsRef(firebaseUser.getUid());
            notificationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    sendNotification(notification.getMessage());
                    DatabaseWriteHelper.deleteNotification(notification);
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
            notificationsRef.addChildEventListener(notificationListener);
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
        notificationsRef.removeEventListener(notificationListener);
        notificationsRef = null;
        notificationListener = null;
        super.onDestroy();
    }

    /**
     * Construct and send notification to app. user's phone
     * See: https://developer.android.com/guide/topics/ui/notifiers/notifications
     * See: https://developer.android.com/training/notify-user/build-notification
     * See: https://developer.android.com/training/notify-user/expanded
     *
     * @param notificationMessage
     */
    private void sendNotification(String notificationMessage) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String channelId = getString(R.string.profile_title);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getBaseContext(), channelId)
                        .setSmallIcon(R.drawable.ic_book_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(notificationMessage)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationMessage))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);

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
