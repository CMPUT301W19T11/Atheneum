package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.atheneum.models.Photo;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BookPhotosRefUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.Iterator;

/**
 * Abstracts operations on a the first photo associated wtih a book retrieved from a Firebase query and
 * provides a LiveData stream to a View (either an Activity or Fragment). This LiveData stream can be
 * observed for changes by adding a listener and then updating the view based on the changes to the data.
 */
public class FirstBookPhotoViewModel extends ViewModel {
    private static final String TAG = FirstBookPhotoViewModel.class.getSimpleName();

    private final String bookID;
    private final FirebaseQueryLiveData firebaseQueryLiveData;

    private final LiveData<Photo> bookPhotoLiveData;

    /**
     * Creates a new instance of FirstBookPhotoViewModel.
     *
     * @param bookID Unique identifier for the book to get first picture for.
     */
    public FirstBookPhotoViewModel(String bookID) {
        this.bookID = bookID;
        firebaseQueryLiveData = new FirebaseQueryLiveData(BookPhotosRefUtils.getBookPhotosRef(bookID).orderByKey().limitToFirst(1));
        bookPhotoLiveData = Transformations.map(firebaseQueryLiveData, new Function<DataSnapshot, Photo>() {
            @Override
            public Photo apply(DataSnapshot dataSnapshot) {
                Log.i(TAG, "in apply()");
                if (dataSnapshot.exists()) {
                    // Since Firebase will return a list of results, even when there is only one result
                    // we have to only extract the first element.
                    // See: https://stackoverflow.com/a/42591662/11039833
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    if (iterator.hasNext()) {
                        return iterator.next().getValue(Photo.class);
                    }
                };
                return null;
            }
        });
    }

    /**
     * Gets book photo live data.
     *
     * @return Lifecycle -aware observable stream of the first photo that the view can observe         for changes.
     */
    public LiveData<Photo> getBookPhotoLiveData() {
        return bookPhotoLiveData;
    }
}
