package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * The activity for users to view their own profiles
 * and other profiles
 */
public class ViewProfileActivity extends AppCompatActivity {
    public static final String USER_ID = "user_id";

    private static final String TAG = "View Profile Activity";

    /**
     * Override onCreate method to produce layout for individual user profiles
     * @param savedInstanceState Bundle environment data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle(getResources().getString(R.string.title_activity_view_profile));

        Intent intent = getIntent();
        String userID = intent.getStringExtra(USER_ID);

        final ImageView profilePicture = findViewById(R.id.user_profile_pic);

        final TextView username = findViewById(R.id.username);
        final TextView phone = findViewById(R.id.phone);
        final TextView borrower_rating = findViewById(R.id.borrower);
        final TextView owner_rating = findViewById(R.id.owner);

        UserViewModelFactory factory = new UserViewModelFactory(userID);
        UserViewModel userViewModel = ViewModelProviders.of(this, factory).get(UserViewModel.class);
        LiveData<User> userLiveData = userViewModel.getUserLiveData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    username.setText(user.getUserName());
                    phone.setText(user.getPhoneNumber());
                    borrower_rating.setText(Double.toString(user.getBorrowerRate()));
                    owner_rating.setText(Double.toString(user.getOwnerRate()));
                    ArrayList<String> photos = user.getPhotos();
                    if (!photos.isEmpty()) {
                        try {
                            String userPic = user.getPhotos().get(0);
                            Bitmap bitmapPhoto = PhotoUtils.DecodeBase64BitmapPhoto(userPic);
                            profilePicture.setImageBitmap(bitmapPhoto);
                        } catch (Exception ignore) {
                            Log.w(TAG, ignore.toString());
                        }
                    }

                }
            }
        });

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser currUser = FirebaseAuthUtils.getCurrentUser();
            FloatingActionButton triggerEditUserProfile = findViewById(R.id.trigger_edit_user_profile);
            Log.d(TAG, "curr user UID is " + currUser.getUid());
            Log.d(TAG, "selected user USERID is " + userID);
            if (!currUser.getUid().equals(userID)) {
                Log.d(TAG, "hiding the FAB");
                triggerEditUserProfile.hide();
            } else {
                triggerEditUserProfile.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Go to edit profile activity when floating action button is clicked
                     * @param v View of FAB
                     */
                    @Override
                    public void onClick(View v) {
                        Intent edit_profile_intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                        startActivity(edit_profile_intent);
                    }
                });
            }
        } else {
            Log.w(TAG, "current user is not authenticated");
        }
    }

    /**
     * Set the action bar title
     * @param title string to set action bar title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}