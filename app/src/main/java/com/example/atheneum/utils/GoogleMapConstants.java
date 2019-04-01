package com.example.atheneum.utils;

/**
 * The Google map constants utility class
 * holds constants used for Google Map API
 */
public class GoogleMapConstants {

    /**
     * The constant ERROR_DIALOG_REQUEST.
     */
//error code: Google Map API not available
    public static final int ERROR_DIALOG_REQUEST = 9001;
    /**
     * The constant PERMISSIONS_REQUEST_ENABLE_GPS.
     */
//request GPS enable
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    /**
     * The constant PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION.
     */
//request location data
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    /**
     * The constant MAPVIEW_BUNDLE_KEY.
     */
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    /**
     * The constant DEFAULT_ZOOM.
     */
//default zoom for google map
    public static final float DEFAULT_ZOOM = 15f;
}
