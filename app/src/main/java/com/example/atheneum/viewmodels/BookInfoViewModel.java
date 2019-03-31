package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class BookInfoViewModel extends ViewModel {
    private static final String TAG = BookInfoViewModel.class.getSimpleName();
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<Book> bookLiveData;
    // Reference to user in Firebase
    private final DatabaseReference bookRef;
    private final String bookID;

    public static String generateViewModelProviderKey(String bookID) {
        return BookInfoViewModel.class.getCanonicalName() + ":" + bookID;
    }


    public BookInfoViewModel(String bookID) {
        this.bookID = bookID;

        bookRef = BooksRefUtils.getBookRef(bookID);
        queryLiveData = new FirebaseQueryLiveData(bookRef);
        bookLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    public void deleteBook(String ownerUserID) {
        DatabaseWriteHelper.deleteBook(ownerUserID, this.bookID);
    }

    public void updateBook(Book book) {
        DatabaseWriteHelper.updateBook(book);
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
