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

    public ConnectionChecker(Context context) {
        this.context = context;
        // Instantiate mConnectivityManager
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

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

