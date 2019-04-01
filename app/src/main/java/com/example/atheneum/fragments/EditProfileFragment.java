package com.example.atheneum.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.CompleteRegistrationActivity;
import com.example.atheneum.utils.CameraHandler;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.ConnectionChecker;
import com.example.atheneum.utils.EmailValidator;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Creates a form to edit user profile information and send it to Firebase
 */
public class EditProfileFragment extends Fragment {
    private static final String TAG = EditProfileFragment.class.getSimpleName();

    private View view;
    private TextView emailAddressPrompt;
    private EditText emailAddressField;
    private EditText phoneNumberField;
    private ImageView profilePicture;
    private EmailValidator emailValidator;
    private CameraHandler cameraHandler;
    private Bitmap bitmapPhoto;
    private OnEditProfileCompleteListener editProfileCompleteListener;
    private UserViewModel userViewModel;

    /**
     * Defines callbacks that handle events that occur after the user profile edit has been completed
     */
    public interface OnEditProfileCompleteListener {
        /**
         * Handles success of editing the profile
         *
         * @param editProfileFragment the edit profile fragment
         */
        void onSuccess(EditProfileFragment editProfileFragment);

        /**
         * Handles failure to edit the profile
         *
         * @param editProfileFragment the edit profile fragment
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
            cameraHandler.dispatchTakePictureIntent();
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
            } else if (emailValidator != null && !emailValidator.validate(emailAddressField)) {
                Log.i(TAG, "Save unsuccessful!");
                return;
            }

            if (FirebaseAuthUtils.isCurrentUserAuthenticated() && userViewModel != null) {
                final User user = userViewModel.getUserLiveData().getValue();
                // Update the user based on form input
                user.setPhoneNumber(phoneNumberField.getText().toString());
                if (emailAddressField.getVisibility() != View.GONE) {
                    user.setUserName(emailAddressField.getText().toString());
                }
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

                FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
                if (!firebaseUser.getEmail().equals(user.getUserName())) {
                    ConnectionChecker connectionChecker = new ConnectionChecker(getContext());
                    if (!connectionChecker.isNetworkConnected()) {
                        Snackbar.make(v, R.string.lost_connection_snackbar_message , Snackbar.LENGTH_LONG).show();
                        Log.w(TAG, "Lost internet connection");
                        return;
                    }

                    firebaseUser.updateEmail(emailAddressField.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Send updated user to Firebase
                                            userViewModel.setUser(user);
                                            if (editProfileCompleteListener != null) {
                                                editProfileCompleteListener.onSuccess(EditProfileFragment.this);
                                            }
                                        } else {
                                            Log.e(TAG, task.getException().toString());
                                            if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                                // See: https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
                                                Snackbar.make(v, R.string.sensitive_operation_snackbar_message , Snackbar.LENGTH_LONG).show();
                                            } else {
                                                Snackbar.make(v, R.string.unknown_error_snackbar_message, Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                } else {
                    // Send updated user to Firebase
                    userViewModel.setUser(user);
                    if (editProfileCompleteListener != null) {
                        editProfileCompleteListener.onSuccess(EditProfileFragment.this);
                    }
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
    private class PictureTakenListener implements CameraHandler.OnPictureTakenListener {

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
     * @return view: the view for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.content_edit_profile, container, false);

        phoneNumberField = view.findViewById(R.id.edit_phone_number);
        phoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        emailAddressField = view.findViewById(R.id.edit_email_address);
        emailAddressPrompt = view.findViewById(R.id.email_address_prompt);
        if (getActivity() instanceof CompleteRegistrationActivity) {
            // Don't allow user to change right after registration
            emailAddressField.setVisibility(View.GONE);
            emailAddressPrompt.setVisibility(View.GONE);
        } else {
            emailValidator = new EmailValidator(emailAddressField);
            // Validate initial values
            emailValidator.validate(emailAddressField);
            emailAddressField.addTextChangedListener(emailValidator);
        }

        FloatingActionButton saveProfileButton = view.findViewById(R.id.save_profile_button);
        saveProfileButton.setOnClickListener(new SaveProfileOnClickListener());

        profilePicture = view.findViewById(R.id.user_profile_picture);
        profilePicture.setOnClickListener(new ProfilePictureOnClickListener());

        cameraHandler = CameraHandler.newInstance(this);
        cameraHandler.setPictureTakenListener(new PictureTakenListener());

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
                        if (emailAddressField.getVisibility() != View.GONE) {
                            Log.i(TAG, "auto-fill email!");
                            emailAddressField.setText(user.getUserName());
                            emailValidator.validate(emailAddressField);
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
        cameraHandler.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handles permissions request made to this fragment
     *
     * @param requestCode Type of permissions request
     * @param permissions Array of strings containing permissions requested
     * @param grantResults Results of permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
