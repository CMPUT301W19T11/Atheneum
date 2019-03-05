package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class BookViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<Book> bookLiveData;
    // Reference to user in Firebase
    private final DatabaseReference bookRef;

    public BookViewModel(String bookID) {
        bookRef = BooksRefUtils.getBookRef(bookID);
        queryLiveData = new FirebaseQueryLiveData(bookRef);
        bookLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    /**
     * Converts the DataSnapshot retrieved from the Firebase Query into a User object
     */
    private class Deserializer implements Function<DataSnapshot, Book> {
        @Override
        public Book apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Book.class);
        }
    }

    @NonNull
    public LiveData<Book> getBookLiveData() {
        return bookLiveData;
    }
}
