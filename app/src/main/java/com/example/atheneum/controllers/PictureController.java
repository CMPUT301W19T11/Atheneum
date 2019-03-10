package com.example.atheneum.controllers;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (fragmentOrActivity instanceof Fragment) {
            Fragment fragment = (Fragment) fragmentOrActivity;
            if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else if (fragmentOrActivity instanceof Activity) {
            Log.i(TAG, "here");
            Activity activity = (Activity) fragmentOrActivity;
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Handle result of event triggered by startActivityForResult() from the fragment/activity.
     * This method must be called in the fragment's/activity's onActivityResult() method or else
     * events from the controller will never be handled.
     *
     * Instantiate an instance of pictureController somewhere in the activity/fragment and then
     * override the onActivityResult() method for the activity/fragment and call the onActivityResult
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
