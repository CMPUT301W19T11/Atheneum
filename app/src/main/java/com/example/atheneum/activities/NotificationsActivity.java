package com.example.atheneum.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.models.Notification;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.NotificationIntentProvider;
import com.example.atheneum.utils.SwipeToDeleteCallback;
import com.example.atheneum.viewmodels.NotificationsViewModel;
import com.example.atheneum.viewmodels.NotificationsViewModelFactory;
import com.example.atheneum.views.adapters.NotificationListAdapter;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = NotificationsActivity.class.getSimpleName();

    private NotificationsViewModel notificationsViewModel;

    private RecyclerView notificationsRecyclerView;
    private NotificationListAdapter notificationListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        subscribeToModel();
    }

    /**
     * Sets up the RecyclerView
     */
    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        notificationsRecyclerView = findViewById(R.id.notifications_recyclerview);
        notificationsRecyclerView.setLayoutManager(linearLayoutManager);
        notificationsRecyclerView.addItemDecoration(
                new DividerItemDecoration(
                        notificationsRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));
        notificationListAdapter = new NotificationListAdapter(this);
        notificationsRecyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnClickListener(new NotificationListAdapter.onClickListener() {
            @Override
            public void onClick(@NonNull Notification notification) {
                Log.i(TAG, "NOTIFICATION CLICKED: " + notification.getMessage());
                showBookInfo(notification);
                makeNotificationSeen(notification);
            }
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(notificationListAdapter));
        itemTouchHelper.attachToRecyclerView(notificationsRecyclerView);
    }

    /**
     * Sets up NotificationsViewModel and observe the LiveData
     */
    private void subscribeToModel() {
        String userID = FirebaseAuthUtils.getCurrentUser().getUid();

        notificationsViewModel = ViewModelProviders
                .of(this, new NotificationsViewModelFactory(userID))
                .get(NotificationsViewModel.class);
        notificationsViewModel.getNotificationLiveData().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notifications) {
                if (notifications != null) {
                    notificationListAdapter.submitList(notifications);
                }
            }
        });
        notificationListAdapter.setNotificationsViewModel(notificationsViewModel);
    }

    /**
     * Starts BookInfoActivity
     */
    private void showBookInfo(Notification notification) {
        Intent showBookIntent = NotificationIntentProvider
                .obtainIntent(this.getApplicationContext(), notification);
        startActivity(showBookIntent);
    }

    /**
     * Make notification seen, used when user taps on notification
     *
     * @param notification
     */
    private void makeNotificationSeen(Notification notification) {
        notificationsViewModel.makeNotificationSeen(notification);
    }
}
