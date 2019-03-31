package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.atheneum.models.Location;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.RootRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.TransactionRefUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import static com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper.writeLocation;
import static com.example.atheneum.viewmodels.FirebaseRefUtils.TransactionRefUtils.getTransactionRef;

public class LocationViewModel extends ViewModel {
    private static final String TAG = "Location View Model";

    private FirebaseQueryLiveData queryLiveData;

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    private LiveData<Location> locationLiveData;

    public LocationViewModel(String BookID) {
        queryLiveData = new FirebaseQueryLiveData(getTransactionRef(BookID).child("location"));
        locationLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, Location> {
        @Override
        public Location apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Location.class);
        }
    }

    public static void addLocation(String BookID, LatLng latLng) {
        Log.d(TAG, "adding new location");

        Location newLocation = new Location(latLng.latitude, latLng.longitude);

        writeLocation(BookID, newLocation);
    }

}
