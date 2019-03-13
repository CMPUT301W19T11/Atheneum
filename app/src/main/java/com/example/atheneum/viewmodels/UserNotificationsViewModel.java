package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.NotificationsRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class UserNotificationsViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<Notification> notificationLiveData;
    // Reference to user in Firebase
    private final DatabaseReference notificationsRef;

    private final String TAG = UserNotificationsViewModel.class.getSimpleName();

    public UserNotificationsViewModel(String userID) {
        notificationsRef = NotificationsRefUtils.getNotificationsRef(userID);
        queryLiveData = new FirebaseQueryLiveData(notificationsRef);
        notificationLiveData = Transformations.map(queryLiveData,
                new UserNotificationsViewModel.Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, Notification> {
        @Override
        public Notification apply(DataSnapshot dataSnapshot) {
            Log.i(TAG, dataSnapshot.toString());
            Log.i(TAG, Long.toString(dataSnapshot.getChildrenCount()));
            if (dataSnapshot.getChildrenCount() > 0)
                return dataSnapshot.getChildren().iterator().next().getValue(Notification.class);
            return null;
        }
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
