package com.example.atheneum.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.fragments.EditProfileFragment;

/**
 * Class for edit profile activity
 * Lets user change phone number and photo
 */
public class EditProfileActivity extends AppCompatActivity {
    static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /**
         * Listener for completion of edits
         * when user is done editing profile in edit profile fragment
         * See: https://stackoverflow.com/a/23592993/
         */
        EditProfileFragment editProfileFragment = (EditProfileFragment)getSupportFragmentManager().findFragmentById(R.id.edit_profile_fragment);
        editProfileFragment.setEditProfileCompleteListener(new EditProfileFragment.OnEditProfileCompleteListener() {
            @Override
            public void onSuccess(EditProfileFragment editProfileFragment) {
                onBackPressed();
            }

            @Override
            public void onFailure(EditProfileFragment editProfileFragment) {
                Log.d(TAG, "FAILED TO EDIT PROFILE");
                onBackPressed();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.edit_profile_fragment, editProfileFragment).commit();
    }

}
