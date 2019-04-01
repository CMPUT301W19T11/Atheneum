package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.atheneum.models.Location;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;


import static com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper.writeLocation;
import static com.example.atheneum.viewmodels.FirebaseRefUtils.TransactionRefUtils.getTransactionRef;

/**
 * The type Location view model.
 * handles writing location data to firebase
 */
public class LocationViewModel extends ViewModel {
    private static final String TAG = "Location View Model";

    private FirebaseQueryLiveData queryLiveData;

    /**
     * Gets location live data.
     *
     * @return the location live data
     */
    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    private LiveData<Location> locationLiveData;

    /**
     * Instantiates a new Location view model.
     *
     * @param BookID the book id
     */
    public LocationViewModel(String BookID) {
        queryLiveData = new FirebaseQueryLiveData(getTransactionRef(BookID).child("location"));
        locationLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, Location> {
        @Override
        public Location apply(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() == false) {
                Log.d(TAG, "Deserializer apply(): location does not exist");
                return null;
            }
            return dataSnapshot.getValue(Location.class);
        }
    }

    /**
     * Add location.
     *
     * @param BookID the book id
     * @param latLng the lat lng
     */
    public static void addLocation(String BookID, LatLng latLng) {
        Log.d(TAG, "adding new location");

        Location newLocation = new Location(latLng.latitude, latLng.longitude);

        writeLocation(BookID, newLocation);
    }

}
