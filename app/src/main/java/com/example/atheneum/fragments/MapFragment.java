package com.example.atheneum.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.atheneum.R;
import com.example.atheneum.activities.MapActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.atheneum.activities.MapActivity.isLocationPermissionGiven;
import static com.example.atheneum.utils.GoogleMapConstants.DEFAULT_ZOOM;
import static com.example.atheneum.utils.GoogleMapConstants.MAPVIEW_BUNDLE_KEY;


/**
 * The Map fragment for displaying Google Maps.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Map Fragment";

    private MapView mMapView;
    private static GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static LatLng goToLocation;
    private static boolean showMarker = false;


    /**
     * Create a new instance of the map fragment.
     * @return the map fragment
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "on Create called");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.google_map_view);

        Log.d(TAG, "initializing Google Map");

        MapsInitializer.initialize(getActivity());

        initGoogleMap(savedInstanceState);

        return view;
    }

    /**
     *  *** IMPORTANT ***
     * MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
     * objects or sub-Bundles.
     * @param savedInstanceState
     */
    private void initGoogleMap(Bundle savedInstanceState){

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        if (!showMarker) {
            getDeviceLocation();
        }

        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        setUpMapView();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public static void moveCamera(LatLng latLng, float zoom) {
//        if (googleMap != null) {
        if (true) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    public static void addMarker(LatLng latLng, float zoom, String title) {
        if (!title.equals("My Location") && googleMap != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
            googleMap.addMarker(markerOptions);
        }
    }

    /**
     * Get device current location
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if(isLocationPermissionGiven()){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            goToLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    /**
     * Set the meeting location to display based on lat/lon from firebase
     * go to the place
     * add a marker
     */
    public static void goToViewLocation(LatLng locationToView) {
        Log.d(TAG, "in gotoviewlocation locationtoview is " + locationToView.toString());
//        moveCamera(locationToView, DEFAULT_ZOOM);
//        addMarker(locationToView, DEFAULT_ZOOM, "Meeting Location");
        goToLocation = locationToView;
        showMarker = true;
    }

    /**
     * Display the meeting location
     * or go to current location
     */
    private void setUpMapView() {
        moveCamera(goToLocation, DEFAULT_ZOOM);
        if (showMarker) {
            addMarker(goToLocation, DEFAULT_ZOOM, "Meeting Location");
        }
    }

}