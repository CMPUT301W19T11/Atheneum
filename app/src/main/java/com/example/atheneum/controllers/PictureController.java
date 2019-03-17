package com.example.atheneum.controllers;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.app.Activity.RESULT_OK;

/**
 * Controller that handles requests to the external camera and provides callbacks to handle the
 * result of these requests.
 *
 * @param <T> Generic type that controller handles. Restricted to Fragment or Activity.
 */
public class PictureController<T> {
    private static final String TAG = PictureController.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 7093;
    private static final int REQUEST_CAMERA_PERMISSION = 6149;

    private final T fragmentOrActivity;
    private OnPictureTakenListener pictureTakenListener;

    /**
     * Provides an interface of callback(s) for handling events from the PictureController
     */
    public interface OnPictureTakenListener {
        /**
         * Callback to handle results of image taken.
         *
         * @param bitmap Photo taken from camera in the form of a bitmap
         */
        public void onPictureTaken(Bitmap bitmap);
    }

    /**
     * Factory method to create a new instance of PictureController given an Activity.
     *
     * @param activity Activity that needs to handle camera requests
     * @return
     */
    public static PictureController<Activity> newInstance(Activity activity) {
        return new PictureController<Activity>(activity);
    }

    /**
     * Factory method to create a new instance of PictureController given an Fragment.
     *
     * @param fragment Fragment that needs to handle camera requests
     * @return
     */
    public static PictureController<Fragment> newInstance(Fragment fragment) {
        return new PictureController<Fragment>(fragment);
    }

    /**
     * Instantiates a new PictureController instance using the specified generic object.
     *
     * To restrict the types that can be passed into this class, we make this constructor private so
     * this forces the use of the provided factory methods to instantiate an instance of the class.
     *
     * Inspired by StackOverflow answer: https://stackoverflow.com/a/9142125/11039833
     *
     * @param fragmentOrActivity Instance of either Fragment or Activity that needs the image taken
     */
    private PictureController(T fragmentOrActivity) {
        this.fragmentOrActivity = fragmentOrActivity;
    }

    /**
     * Sets the interface of callback(s) to handle events from controller
     *
     * @param pictureTakenListener
     */
    public void setPictureTakenListener(OnPictureTakenListener pictureTakenListener) {
        this.pictureTakenListener = pictureTakenListener;
    }

    /**
     * Sends request for external camera activity and passes result of request to the Fragment
     * or the Activity.
     */
    public void dispatchTakePictureIntent() {
        // See: https://developer.android.com/training/camera/photobasics
        // See: https://developer.android.com/training/permissions/requesting

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (fragmentOrActivity instanceof Fragment) {
            Fragment fragment = (Fragment) fragmentOrActivity;
            if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // See: https://stackoverflow.com/a/46046597/11039833
                fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else if (fragmentOrActivity instanceof Activity) {
            Log.i(TAG, "here");
            Activity activity = (Activity) fragmentOrActivity;
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Handles result of permission request made to the fragment/activity.
     *
     * Instantiate an instance of pictureController somewhere in the activity/fragment and then
     * override the onRequestPermissionsResult() method for the activity/fragment and call the onRequestPermissionsResult()
     * method of the picture controller instance in order to handle events.
     *
     * Example:
     *
     * {@code
     *     @Override
     *     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     *         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *         pictureController.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *     }
     * }
     *
     * @param requestCode Type of permissions request
     * @param permissions Array of strings containing permissions requested
     * @param grantResults Results of permissions granted
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                dispatchTakePictureIntent();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.i(TAG, "Camera permission denied!");
            }
        }
    }

    /**
     * Handle result of event triggered by startActivityForResult() from the fragment/activity.
     * This method must be called in the fragment's/activity's onActivityResult() method or else
     * events from the controller will never be handled.
     *
     * Instantiate an instance of pictureController somewhere in the activity/fragment and then
     * override the onActivityResult() method for the activity/fragment and call the onActivityResult()
     * method of the picture controller instance in order to handle events.
     *
     * Example:
     *
     * {@code
     *      @Override
     *      public void onActivityResult(int requestCode, int resultCode, Intent data) {
     *          super.onActivityResult(requestCode, resultCode, data);
     *          pictureController.onActivityResult(requestCode, resultCode, data);
     *      }
     * }
     *
     * @param requestCode Request Code for starting activity/fragment
     * @param resultCode Result of starting activity/fragment
     * @param data Data obtained from the request
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.i(TAG, "onActivityResult!");
            if (pictureTakenListener != null) {
                Log.i(TAG, "call pictureTakenListener!");
                pictureTakenListener.onPictureTaken(imageBitmap);
            }
        }
    }
}
