package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;

public class TestAggregateViewModel extends ViewModel {
    private final FirebaseQueryLiveData borrowerQueryLiveData;
    private final LiveData<User> borrowerLiveData;
    private final FirebaseQueryLiveData bookQueryLiveData;
    private final LiveData<Book> bookLiveData;

    private final MediatorLiveData<Pair<User, Book>> borrowerBookPairLiveData;

    public TestAggregateViewModel(String borrowerID, String bookID) {
        borrowerBookPairLiveData = new MediatorLiveData<>();
        borrowerBookPairLiveData.setValue(new Pair<User, Book>(null, null));

        // Update borrower
        borrowerQueryLiveData = new FirebaseQueryLiveData(UsersRefUtils.getUsersRef(borrowerID));
        borrowerLiveData = Transformations.map(borrowerQueryLiveData, new Function<DataSnapshot, User>() {
            @Override
            public User apply(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(User.class);
            }
        });
        borrowerBookPairLiveData.addSource(borrowerLiveData, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    Pair<User, Book> pair = borrowerBookPairLiveData.getValue();
                    borrowerBookPairLiveData.setValue(new Pair<>(user, pair.second));
                }
            }
        });

        // Update book
        bookQueryLiveData = new FirebaseQueryLiveData(BooksRefUtils.getBookRef(bookID));
        bookLiveData = Transformations.map(bookQueryLiveData, new Function<DataSnapshot, Book>() {
            @Override
            public Book apply(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(Book.class);
            }
        });
        borrowerBookPairLiveData.addSource(bookLiveData, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    Pair<User, Book> pair = borrowerBookPairLiveData.getValue();
                    borrowerBookPairLiveData.setValue(new Pair<>(pair.first, book));
                }
            }
        });
    }

    public LiveData<Pair<User, Book>> getBorrowerBookPairLiveData() {
        return borrowerBookPairLiveData;
    }

}
