package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.atheneum.R;
import com.example.atheneum.controllers.PictureController;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


/**
 * Creates a form to edit user profile information and send it to Firebase
 */
public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "Edit Profile Activity";

    private View view;
    private EditText phoneNumberField;
    private ImageView profilePicture;
    private PictureController pictureController;
    private Bitmap bitmapPhoto;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
