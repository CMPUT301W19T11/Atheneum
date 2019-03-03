package com.example.atheneum.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.controllers.PictureController;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.EmailValidator;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 *
 * Creates a form to edit user profile information and send it to Firebase
 */
public class EditProfileFragment extends Fragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    private View view;
    private EditText phoneNumberField;
    private ImageView profilePicture;
    private PictureController pictureController;
    private Bitmap bitmapPhoto;
    private OnEditProfileCompleteListener editProfileCompleteListener;
    private UserViewModel userViewModel;

    /**
     * Defines callbacks that handle events that occur after the user profile edit has been completed
     */
    public interface OnEditProfileCompleteListener {
        /**
         * Handles success of editing the profile
         */
        void onSuccess(EditProfileFragment editProfileFragment);

        /**
         * Handles failure to edit the profile
         */
        void onFailure(EditProfileFragment editProfileFragment);
    }

    /**
     * Handles events resulting from clicking the image view on the form
     */
    private class ProfilePictureOnClickListener implements View.OnClickListener {

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Profile image clicked!");
            pictureController.dispatchTakePictureIntent();
        }
    }

    /**
     * Handles on click events from the save button
     */
    private class SaveProfileOnClickListener implements View.OnClickListener {

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(final View v) {
            // Force user to update phone number for now, should relax later on
            if (TextUtils.isEmpty(phoneNumberField.getText())) {
                Log.i(TAG, "Save unsuccessful!");
                Snackbar.make(v, "Phone number can't be empty!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "Save successful!");

            if (userViewModel != null) {
                User user = userViewModel.getUserLiveData().getValue();
                // Update the user based on form input
                user.setPhoneNumber(phoneNumberField.getText().toString());
                if (bitmapPhoto != null) {
                    String encodedPhoto = PhotoUtils.EncodeBitmapPhotoBase64(bitmapPhoto);
                    ArrayList<String> photos = user.getPhotos();
                    if (photos.isEmpty()) {
                        photos.add(encodedPhoto);
                    } else {
                        photos.set(0, encodedPhoto);
                    }
                    user.setPhotos(photos);
                }
                // Send updated user to Firebase
                userViewModel.setUser(user);
                if (editProfileCompleteListener != null) {
                    editProfileCompleteListener.onSuccess(EditProfileFragment.this);
                }
            } else {
                Log.w(TAG, "If the user is in this fragment, they should already be logged in!");
                if (editProfileCompleteListener != null) {
                    editProfileCompleteListener.onFailure(EditProfileFragment.this);
                }
            }
        }
    }

    /**
     * Handles image taken from external camera app
     */
    private class PictureTakenListener implements PictureController.OnPictureTakenListener {

        @Override
        public void onPictureTaken(Bitmap bitmap) {
            Log.i(TAG, "onPictureTaken");
            bitmapPhoto = bitmap;
            profilePicture.setImageBitmap(bitmap);
        }
    }

    /**
     * Instantiate a new instance of EditProfileFragment
     */
    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Sets OnEditProfileCompleteListener
     *
     * @param editProfileCompleteListener New listener
     */
    public void setEditProfileCompleteListener(OnEditProfileCompleteListener editProfileCompleteListener) {
        this.editProfileCompleteListener = editProfileCompleteListener;
    }

    /**
     * Handles events relating to creation of the Fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile2, container, false);

        phoneNumberField = view.findViewById(R.id.edit_phone_number);
        phoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        FloatingActionButton saveProfileButton = view.findViewById(R.id.save_profile_button);
        saveProfileButton.setOnClickListener(new SaveProfileOnClickListener());

        profilePicture = view.findViewById(R.id.user_profile_picture);
        profilePicture.setOnClickListener(new ProfilePictureOnClickListener());

        pictureController = PictureController.newInstance(this);
        pictureController.setPictureTakenListener(new PictureTakenListener());

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firebaseUser.getUid());
            userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel.class);
            final LiveData<User> userLiveData = userViewModel.getUserLiveData();
            userLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    Log.i(TAG, "in Observer!");
                    // Auto-fill profile
                    if (user != null) {
                        Log.i(TAG, "Auto-fill profile!!");
                        phoneNumberField.setText(user.getPhoneNumber());
                        ArrayList<String> photos = user.getPhotos();
                        if (!photos.isEmpty()) {
                            Bitmap image = PhotoUtils.DecodeBase64BitmapPhoto(photos.get(0));
                            profilePicture.setImageBitmap(image);
                        }
                    }
                    // Remove the observer after auto-fill so that the image view is properly
                    // updated when the user takes a picture.
                    // See: https://stackoverflow.com/a/47872807/11039833
                    // Simulates using addSingleEventValueListener on a database reference
                    userLiveData.removeObserver(this);
                }
            });
        } else {
            Log.w(TAG, "Shouldn't happen!");
        }

        return view;
    }

    /**
     * Handles requests made to activity.
     *
     * @param requestCode Type of request made to activity
     * @param resultCode Result of request to activity
     * @param data Data from request to activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pictureController.onActivityResult(requestCode, resultCode, data);
    }
}
