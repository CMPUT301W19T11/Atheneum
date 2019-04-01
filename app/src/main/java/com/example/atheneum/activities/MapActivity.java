package com.example.atheneum.activities;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.fragments.MapFragment;
import com.example.atheneum.models.Location;
import com.example.atheneum.viewmodels.LocationViewModel;
import com.example.atheneum.viewmodels.LocationViewModelFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.atheneum.fragments.MapFragment.addMarker;
import static com.example.atheneum.fragments.MapFragment.goToViewLocation;
import static com.example.atheneum.fragments.MapFragment.moveCamera;
import static com.example.atheneum.utils.GoogleMapConstants.DEFAULT_ZOOM;
import static com.example.atheneum.utils.GoogleMapConstants.ERROR_DIALOG_REQUEST;
import static com.example.atheneum.utils.GoogleMapConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.atheneum.utils.GoogleMapConstants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.example.atheneum.viewmodels.LocationViewModel.addLocation;



/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 *
 * See: https://gist.github.com/mitchtabian/2b9a3dffbfdc565b81f8d26b25d059bf
 */
public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * The constant TAG.
     */
    public static final String TAG = "Map Activity";

    private static boolean locationPermissionGiven = false;

    private String bookID;

    LocationViewModel locationViewModel;

    LatLng locationToView;

    boolean viewOnly = false;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "google map connection failed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "creating Map Activity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        Intent mapIntent = getIntent();

        bookID = mapIntent.getExtras().getString("BookID");
        Log.d(TAG, "bookID is: " + bookID);

        locationViewModel = ViewModelProviders
                .of(this, new LocationViewModelFactory(bookID))
                .get(LocationViewModel.class);
        viewOnly = mapIntent.getExtras().getBoolean("ViewOnly");
        Log.d(TAG, "viewonly is: " + String.valueOf(viewOnly));
        if (viewOnly == true) {
            RelativeLayout searchBar = (RelativeLayout) findViewById(R.id.search_bar);
            searchBar.setVisibility(View.INVISIBLE);
            inflateMapFragment();
            viewLocation();
        } else {
            initSearchListener();
        }

    }

    /**
     * //prevent going back until location set
     */
    @Override
    public void onBackPressed() {
        if (viewOnly == true) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Must set a location", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes editor action listener for searched text
     * See: https://stackoverflow.com/questions/15901863/oneditoractionlistener-not-working
     */
    private void initSearchListener() {
        Log.d(TAG, "initializing search listener");

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                setNewLocation(place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    /**
     * Get the meetinglocation
     * from Firebase
     */
    private void viewLocation() {
        Log.d(TAG, "Getting location in MapActivity with view location");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("transactions").child(bookID).child("location");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() == false) {
                    Log.e(TAG, "viewLocation failed datasnapshot location does not exist");
                }
//                double lat = dataSnapshot.child("lat").getValue(double.class);
//                double lon = dataSnapshot.child("lon").getValue(double.class);
                Location location = dataSnapshot.getValue(Location.class);
                locationToView = new LatLng(location.getLat(), location.getLon());
                Log.d(TAG, "after onDataChange locationtoview is " + locationToView.toString());
                goToViewLocation(locationToView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Sets a new geo location for the meeting
     * go to the place
     * add a marker
     * add it to firebase
     */
    private void setNewLocation(String locationName) {
        Log.d(TAG, "Setting location in MapActivity with set new location");

        String searchLocation = locationName;

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchLocation, 1);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            Log.d(TAG, "setNewLocation got " + address.toString());

            addMarker(new LatLng(address.getLatitude(), address.getLongitude()), "Meeting Location");
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);

            addLocation(bookID, new LatLng(address.getLatitude(), address.getLongitude()));

            viewOnly = true;
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (locationPermissionGiven) {
                Log.d(TAG, "inflate Map onResume");
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
     * User response captured in on ActivityResult method
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
            Log.d(TAG, "inflate Map after getting location permission");
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
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
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
                    Log.d(TAG, "inflate Map onActivityResult");
                    inflateMapFragment();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    /**
     * create map fragment
     */
    public void inflateMapFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new MapFragment()).commit();
    }

    public static boolean isLocationPermissionGiven(){
        return locationPermissionGiven;
    }

}