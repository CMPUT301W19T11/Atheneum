package com.example.atheneum.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for checking whether or not device is connected to the internet.
 * 
 */
public class ConnectionChecker {
    // taken from https://stackoverflow.com/questions/14571478/using-google-books-api-in-android
    private ConnectivityManager mConnectivityManager;
    private Context context;

    /**
     * Constructor for connection checker. This is needed because the getSystemService call
     * needs the current context in order to be used.
     * @param context the context in which ConnectionChecker is instantiated
     */
    public ConnectionChecker(Context context) {
        this.context = context;
        // Instantiate mConnectivityManager
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Checks if the device is connected to the Internet using the ConnectivityManager initialized
     * in the constructor. 
     *
     * @return whether or not the device is connected to the Internet.
     */
    public boolean isNetworkConnected(){
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }

}

