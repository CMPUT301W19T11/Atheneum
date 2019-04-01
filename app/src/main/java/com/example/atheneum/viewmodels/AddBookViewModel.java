package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * The type Add book view model.
 * view model for handling adding of books
 */
public class AddBookViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<User> ownerLiveData;
    // Reference to user in Firebase
    private final DatabaseReference userRef;

    /**
     * Creates an instance of UserViewModel that watches for changes to data held in a Firebase
     * Query, transforms it to a User object, and allows a view to observer for changes on this
     * data in order to update it's UI elements.
     *
     * @param userID User ID of user queried in the database
     */
    public AddBookViewModel(String userID) {
        userRef = UsersRefUtils.getUsersRef(userID);
        queryLiveData = new FirebaseQueryLiveData(userRef);
        ownerLiveData = Transformations.map(queryLiveData, new AddBookViewModel.Deserializer());
    }

    /**
     * Converts the DataSnapshot retrieved from the Firebase Query into a User object
     */
    private class Deserializer implements Function<DataSnapshot, User> {
        @Override
        public User apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(User.class);
        }
    }

    /**
     * Add book.
     *
     * @param owner the owner
     * @param book  the book
     */
    public void addBook(User owner, Book book) {
        DatabaseWriteHelper.addNewBook(owner, book);
    }

    /**
     * Gets owner live data.
     *
     * @return Observable User data from Firebase
     */
    @NonNull
    public LiveData<User> getOwnerLiveData() {
        return ownerLiveData;
    }
}
