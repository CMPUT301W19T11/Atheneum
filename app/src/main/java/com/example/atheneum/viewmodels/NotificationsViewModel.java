package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.NotificationsRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<List<Notification>> notificationLiveData;
    // Reference to user in Firebase
    private final DatabaseReference notificationsRef;

    private final String TAG = NotificationsViewModel.class.getSimpleName();

    public NotificationsViewModel(String userID) {
        notificationsRef = NotificationsRefUtils.getNotificationsRef(userID);
        queryLiveData = new FirebaseQueryLiveData(notificationsRef);
        notificationLiveData = Transformations.map(queryLiveData, new Function<DataSnapshot, List<Notification>>() {
            @Override
            public List<Notification> apply(DataSnapshot dataSnapshot) {
                List<Notification> notificationList = new ArrayList<Notification>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    notificationList.add(data.getValue(Notification.class));
                }
                return notificationList;
            }
        });
    }

    /**
     *
     * @return Observable User data from Firebase
     */
    @NonNull
    public LiveData<List<Notification>> getNotificationLiveData() {
        return notificationLiveData;
    }
}
