package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.NotificationsRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    private final LiveData<List<Notification>> notificationLiveData;
    // Notifications query for firebase
    private final Query notificationsRef;

    private final String TAG = NotificationsViewModel.class.getSimpleName();

    public NotificationsViewModel(String userID) {
        notificationsRef = NotificationsRefUtils.getNotificationsRef(userID);
        queryLiveData = new FirebaseQueryLiveData(notificationsRef);
        notificationLiveData = Transformations.map(queryLiveData, new Function<DataSnapshot, List<Notification>>() {
            @Override
            public List<Notification> apply(DataSnapshot dataSnapshot) {
                List<Notification> notificationList = new ArrayList<Notification>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // reverse elements to for reverse-chronological time
                    notificationList.add(0, data.getValue(Notification.class));
                }
                return notificationList;
            }
        });
    }

    public void makeNotificationSeen(Notification notification) {
        if (!notification.getIsSeen()) {
            notification.setIsSeen(true);
            DatabaseWriteHelper.makeNotificationSeen(notification);
        }
    }

    /**
     *
     * @return Observable list of Notification data from Firebase
     */
    @NonNull
    public LiveData<List<Notification>> getNotificationLiveData() {
        return notificationLiveData;
    }
}
