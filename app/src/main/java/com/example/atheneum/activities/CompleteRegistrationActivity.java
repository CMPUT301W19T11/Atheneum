package com.example.atheneum.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.atheneum.R;
import com.example.atheneum.fragments.EditProfileFragment;

/**
 * Completes registration for user by asking them to edit their profile information
 */
public class CompleteRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // See: https://stackoverflow.com/a/23592993/11039833
        EditProfileFragment editProfileFragment = (EditProfileFragment)getSupportFragmentManager().findFragmentById(R.id.edit_profile_fragment);
        editProfileFragment.setEditProfileCompleteListener(new EditProfileFragment.OnEditProfileCompleteListener() {
            private void startMainActivity(EditProfileFragment editProfileFragment) {
                Activity activity = editProfileFragment.getActivity();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onSuccess(EditProfileFragment editProfileFragment) {
                startMainActivity(editProfileFragment);
            }

            @Override
            public void onFailure(EditProfileFragment editProfileFragment) {
                startMainActivity(editProfileFragment);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.edit_profile_fragment, editProfileFragment).commit();
    }

}
