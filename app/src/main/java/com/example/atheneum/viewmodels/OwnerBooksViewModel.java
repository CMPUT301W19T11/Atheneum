package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Abstracts operations on a list of books owned by a user retrieved from a Firebase query and provides a LiveData stream
 * to a View (either an Activity or Fragment). This livedata stream can be observed for changes by
 * adding a listener and then updating the view based on the changes to the data. The list of books
 * queried by Firebase is found by books matching the owner's user id.
 */
public class OwnerBooksViewModel extends ViewModel {
    private static final String TAG = OwnerBooksViewModel.class.getSimpleName();

    private final String ownerID;
    private final FirebaseQueryLiveData firebaseQueryLiveData;
    private final LiveData<ArrayList<Book>> ownerBooksLiveData;

    /**
     * Create a new instance of OwnerBooksViewModel.
     *
     * @param ownerID User ID of the owner.
     */
    public OwnerBooksViewModel(String ownerID) {
        this.ownerID = ownerID;
        firebaseQueryLiveData = new FirebaseQueryLiveData(BooksRefUtils.getOwnerBooksRef(ownerID));
        ownerBooksLiveData = Transformations.map(firebaseQueryLiveData, new Function<DataSnapshot, ArrayList<Book>>() {
            @Override
            public ArrayList<Book> apply(DataSnapshot dataSnapshot) {
                ArrayList<Book> ownerBooks = null;
                if (dataSnapshot.exists()) {
                    ownerBooks = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ownerBooks.add(data.getValue(Book.class));
                    }
                }
                return ownerBooks;
            }
        });
    }

    /**
     * @return Lifecycle-aware observable stream of {@code ArrayList<Book>} that the view can observe
     *         for changes. The books in this list are owned by the owner.
     */
    public LiveData<ArrayList<Book>> ownerBooksLiveData() {
        return ownerBooksLiveData;
    }
}
