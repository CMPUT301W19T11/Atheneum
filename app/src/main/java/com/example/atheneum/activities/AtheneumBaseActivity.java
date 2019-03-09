package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.atheneum.R;
import com.example.atheneum.models.Notification;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserNotificationsViewModel;
import com.example.atheneum.viewmodels.UserNotificationsViewModelFactory;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AtheneumBaseActivity extends AppCompatActivity {

//    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.i("Base Activity", "in oncreate");
        // Update user information in the navbar
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            UserNotificationsViewModelFactory factory = new UserNotificationsViewModelFactory(firebaseUser.getUid());
            UserNotificationsViewModel userNotificationsViewModel =
                    ViewModelProviders.of(this, factory).get(UserNotificationsViewModel.class);
            LiveData<Notification> userNotificationsLiveData = userNotificationsViewModel.getNotificationLiveData();
            userNotificationsLiveData.observe(this, new Observer<Notification>() {
                @Override
                public void onChanged(@Nullable Notification notification) {
                    if (notification == null) {
                        Log.i("Base Activity", "notification is null");
                    } else {
                        Log.i("Base Activity", "notification is not null");
                    }
                }
            });
        } else {
            Log.w("Base Activity", "Shouldn't happen!");
        }
    }
}
