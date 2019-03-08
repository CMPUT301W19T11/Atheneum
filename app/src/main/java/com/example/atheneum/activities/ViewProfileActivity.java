package com.example.atheneum.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.fragments.EditProfileFragment;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.google.firebase.auth.FirebaseUser;

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
        setActionBarTitle(getResources().getString(R.string.title_activity_view_profile));

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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}