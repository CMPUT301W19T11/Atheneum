package com.example.atheneum.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.fragments.MapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.atheneum.utils.GoogleMapConstants.DEFAULT_ZOOM;
import static com.example.atheneum.utils.GoogleMapConstants.ERROR_DIALOG_REQUEST;
import static com.example.atheneum.utils.GoogleMapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.atheneum.utils.GoogleMapConstants.PERMISSIONS_REQUEST_ENABLE_GPS;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 *
 * See: https://gist.github.com/mitchtabian/2b9a3dffbfdc565b81f8d26b25d059bf
 */
public class MapActivity extends AppCompatActivity {

    /**
     * The constant TAG.
     */
    public static final String TAG = "Map Activity";
    private boolean locationPermissionGiven = false;

    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "creating Map Activity");
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        searchText = (EditText) findViewById(R.id.search_text);
        searchText.setMaxLines(1);
        initSearchListener();
    }

    /**
     * Initializes editor action listener for searched text
     * See: https://stackoverflow.com/questions/15901863/oneditoractionlistener-not-working
     */
    private void initSearchListener() {
        Log.d(TAG, "initializing search listener");
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    Log.d(TAG, "call getGeo");
                    getGeoLocation();
                    return true;
                }
                return false;
            }
        });
    }

    //TODO: persistent location data - Firebase
    private void getGeoLocation() {
        String searchLocation = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            Log.d(TAG, "getGeoLocation got " + address.toString());

            //TODO: replace meeting location placeholder with owner/requester name
            MapFragment.moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, "Meeting Location");

//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (locationPermissionGiven) {
                inflateMapFragment();
            }
            else {
                getLocationPermission();
            }
        }
    }

    /**
     * Check if Google Maps services is enabled
     * in isServicesOK method
     *
     * then checks that GPS is enabled
     * in isMapsEnabled method
     *
     * @return boolean: true if google map services enabled, else false
     */
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    /**
     * Ask for user to enable GPS in dialog box
     * Create activity/intent to enable GPS
     * User response captured in onActivityResult method
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Check if GPS enabled for Atheneum
     *
     * @return boolean: true if GPS enabled, else false
     */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /**
     * Request location permission to get user device location
     * Get response in onRequestPermissionsResult method
     */
    private void getLocationPermission() {
        /* The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGiven = true;
            inflateMapFragment();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Check if google services is installed on device
     *
     * @return boolean : true if connected to google services, else false
     */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGiven = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGiven = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationPermissionGiven){
                    inflateMapFragment();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    private void inflateMapFragment() {
//        MapFragment mapFragment = MapFragment.newInstance();
        Log.d(TAG, "inflate Map Fragment Called");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new MapFragment()).addToBackStack("MapFragment").commit();
    }

}