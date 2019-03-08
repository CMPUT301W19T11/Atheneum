package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Notification;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class NotificationsViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<Notification> notificationLiveData;
    // Reference to user in Firebase
    private final DatabaseReference notificationsRef;

    /**
     * Creates an instance of UserViewModel that watches for changes to data held in a Firebase
     * Query, transforms it to a User object, and allows a view to observer for changes on this
     * data in order to update it's UI elements.
     *
     * @param userID User ID of user queried in the database
     */
    public NotificationsViewModel(String userID) {
        notificationsRef = UsersRefUtils.getUsersRef(userID);
        queryLiveData = new FirebaseQueryLiveData(notificationsRef);
        notificationLiveData = Transformations.map(queryLiveData,
                new NotificationsViewModel.Deserializer());
    }

    /**
     * Converts the DataSnapshot retrieved from the Firebase Query into a Notification object
     */
    private class Deserializer implements Function<DataSnapshot, Notification> {
        @Override
        public Notification apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Notification.class);
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
    public LiveData<Notification> getNotificationLiveData() {
        return notificationLiveData;
    }
}
