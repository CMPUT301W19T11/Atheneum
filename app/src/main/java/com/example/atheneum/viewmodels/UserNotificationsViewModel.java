package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;

public class UserNotificationsViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<String> notificationLiveData;
    // Reference to user in Firebase
    private final DatabaseReference notificationsRef;

    private final String TAG = UserNotificationsViewModel.class.getSimpleName();

    /**
     * Creates an instance of UserViewModel that watches for changes to data held in a Firebase
     * Query, transforms it to a User object, and allows a view to observer for changes on this
     * data in order to update it's UI elements.
     *
     * @param userID User ID of user queried in the database
     */
    public UserNotificationsViewModel(String userID) {
        notificationsRef = UsersRefUtils.getUsersRef(userID);
        queryLiveData = new FirebaseQueryLiveData(notificationsRef);
        notificationLiveData = Transformations.map(queryLiveData,
                new UserNotificationsViewModel.Deserializer());
    }

    /**
     * Converts the DataSnapshot retrieved from the Firebase Query into a Notification object
     *
     * See: https://stackoverflow.com/questions/30744224/how-to-retrieve-a-list-object-from-the-firebase-in-android
     */
    private class Deserializer implements Function<DataSnapshot, String> {
        @Override
        public String apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(String.class);
        }
    }

    /**
     * Removes notification from Firebase
     *
     * @param notification
     */
    public void deleteNotification(Notification notification) {
        notificationsRef
                .child(notification.getNotificationReceiverID())
                .child(notification.getNotificationID())
                .removeValue();
    }

    /**
     *
     * @return Observable User data from Firebase
     */
    @NonNull
    public LiveData<String> getNotificationLiveData() {
        Log.i(TAG, "where what when bhhbuh");
        return notificationLiveData;
    }
}
