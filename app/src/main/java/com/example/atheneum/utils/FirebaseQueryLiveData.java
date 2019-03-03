package com.example.atheneum.utils;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Provides a LiveData wrapper for a DataSnapshot obtained from a Firebase Query
 *
 * See: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 *      https://firebase.googleblog.com/2017/12/using-android-architecture-components_20.html
 *      https://firebase.googleblog.com/2017/12/using-android-architecture-components_22.html
 */
public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = FirebaseQueryLiveData.class.getSimpleName();

    private boolean listenRemovePending = false;
    // Query to observe
    private final Query query;
    // Reads result of query
    private final MyValueEventListener listener = new MyValueEventListener();
    // Android OS component that handles a runnable function that should either be called after
    // some delay or not called at all
    private final Handler handler = new Handler();
    // Callback that removes the removes the event listener from the query
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            listenRemovePending = false;
        }
    };

    /**
     * Create a new instance of FirebaseQueryLiveData
     *
     * @param query Query to observe
     */
    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    /**
     * Create a new instance of FirebaseQueryLiveData
     *
     * Note: DatabaseReference is a subclass of Query
     *
     * @param ref Database referenence to observe
     */
    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    /**
     * Handles LifecycleOwner of the LiveData (ex. an Activity or a Fragment)
     * becoming active.
     */
    @Override
    protected void onActive() {
        Log.i(TAG, "onActive");
        if (listenRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            query.addValueEventListener(listener);
        }
        listenRemovePending = false;
    }

    /**
     * Handles LifecycleOwner of the LiveData (ex. an Activity or a Fragment)
     * becoming inactive.
     */
    @Override
    protected void onInactive() {
        Log.i(TAG, "onInactive");
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenRemovePending = true;
    }

    /**
     * Callback to read DataSnapshot retrieved from query and update the value of the LiveData
     */
    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
