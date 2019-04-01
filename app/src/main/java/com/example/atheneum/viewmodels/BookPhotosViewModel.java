package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Photo;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BookPhotosRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Abstracts operations on a list of photos associated wtih a book retrieved from a Firebase query and
 * provides a LiveData stream to a View (either an Activity or Fragment). This LiveData stream can be
 * observed for changes by adding a listener and then updating the view based on the changes to the data.
 */
public class BookPhotosViewModel extends ViewModel {
    private final String bookID;
    private final FirebaseQueryLiveData firebaseQueryLiveData;
    private final LiveData<ArrayList<Photo>> photosLiveData;

    /**
     * Creates a new instance of BookPhotosViewModel
     *
     * @param bookID Book ID of book of interest
     */
    public BookPhotosViewModel(String bookID) {
        this.bookID = bookID;
        firebaseQueryLiveData = new FirebaseQueryLiveData(BookPhotosRefUtils.getBookPhotosRef(bookID));
        photosLiveData = Transformations.map(firebaseQueryLiveData, new Function<DataSnapshot, ArrayList<Photo>>() {
            @Override
            public ArrayList<Photo> apply(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Photo> photos = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        photos.add(data.getValue(Photo.class));
                    }
                    return photos;
                }
                return null;
            }
        });
    }

    /**
     * Gets photos live data.
     *
     * @return Lifecycle -aware observable stream of {@code ArrayList<String>} that the view can observe         for changes.
     */
    public LiveData<ArrayList<Photo>> getPhotosLiveData() {
        return photosLiveData;
    }

    /**
     * Add new photo.
     *
     * @param newBitmap New picture taken from camera.
     */
    public void addPhoto(Bitmap newBitmap) {
        DatabaseWriteHelper.addBookPhoto(bookID, newBitmap);
    }

    /**
     * Update a photo associated with the book.
     *
     * @param photo     Photo to update.
     * @param newBitmap New picture taken from camera.
     */
    public void updatePhoto(Photo photo, Bitmap newBitmap) {
        DatabaseWriteHelper.updateBookPhoto(bookID, photo, newBitmap);
    }

    /**
     * Delete a photo associated with the book.
     *
     * @param photo Photo to remove.
     */
    public void deletePhoto(Photo photo) {
        DatabaseWriteHelper.deleteBookPhoto(bookID, photo);
    }
}
