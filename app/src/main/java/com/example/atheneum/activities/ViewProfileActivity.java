package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.controllers.PictureController;
import com.example.atheneum.fragments.EditProfileFragment;
import com.example.atheneum.fragments.HomeFragment;
import com.example.atheneum.fragments.ViewProfileFragment;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


/**
 * The activity for users to view their own profiles
 */
public class ViewProfileActivity extends AppCompatActivity {
    private static final String TAG = "View Profile Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
//        final TextView nav_username = (TextView) ((View) headerView).findViewById(R.id.nav_user_name);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        ImageView profilePicture = findViewById(R.id.user_profile_pic);
        try {
            String userPic = user.getPhotos().get(0);
            Bitmap bitmapPhoto = StringToBitMap(userPic);
            profilePicture.setImageBitmap(bitmapPhoto);
        } catch (Exception ignore) {

        }

        TextView username = findViewById(R.id.username);
        TextView phone = findViewById(R.id.phone);
        TextView borrower_rating = findViewById(R.id.borrower);
        TextView owner_rating = findViewById(R.id.owner);

        username.setText(user.getUserName());
        phone.setText(user.getPhoneNumber());

        borrower_rating.setText(Double.toString(user.getBorrowerRate()));
        owner_rating.setText(Double.toString(user.getOwnerRate()));

        FirebaseUser currUser = FirebaseAuthUtils.getCurrentUser();
        FloatingActionButton triggerEditUserProfile = findViewById(R.id.trigger_edit_user_profile);
        Log.d(TAG, "curr user UID is " + currUser.getUid());
        Log.d(TAG, "selected user USERID is " + user.getUserID());
        if (!currUser.getUid().equals(user.getUserID())) {
            Log.d(TAG, "hiding the FAB");
            triggerEditUserProfile.hide();
        } else {
            triggerEditUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditProfileFragment editProfileFragment = new EditProfileFragment();
                    editProfileFragment.setEditProfileCompleteListener(new EditProfileFragment.OnEditProfileCompleteListener() {
                        @Override
                        public void onSuccess(EditProfileFragment fragment) {
                            onBackPressed();
                        }

                        @Override
                        public void onFailure(EditProfileFragment fragment) {
                            onBackPressed();
                        }
                    });
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, editProfileFragment).addToBackStack("EditProfile").commit();

                }
            });
        }

        // Update user information in the navbar
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firebaseUser.getUid());
            UserViewModel userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel.class);
            LiveData<User> userLiveData = userViewModel.getUserLiveData();
            userLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    Log.i(TAG, "in Observer!");
                    if (user != null) {
//                        nav_username.setText(user.getUserName());

                        ArrayList<String> photos = user.getPhotos();
                        if (!photos.isEmpty()) {
                            Bitmap profilePic = PhotoUtils.DecodeBase64BitmapPhoto(photos.get(0));
                            ImageView imageView = (ImageView) ((View) navigationView).findViewById(R.id.nav_user_profile_picture);
                            imageView.setImageBitmap(profilePic);
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, "Shouldn't happen!");
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}