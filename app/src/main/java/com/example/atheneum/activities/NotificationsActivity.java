package com.example.atheneum.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.atheneum.R;
import com.example.atheneum.models.Notification;
import com.example.atheneum.viewmodels.NotificationsViewModel;
import com.example.atheneum.views.adapters.NotificationListAdapter;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = NotificationsActivity.class.getSimpleName();

    private View view;
    private NotificationsViewModel notificationsViewModel;

    private RecyclerView notificationsRecyclerView;
    private NotificationListAdapter notificationListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

}
