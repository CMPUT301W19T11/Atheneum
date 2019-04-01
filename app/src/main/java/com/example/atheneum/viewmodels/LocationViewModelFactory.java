package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * The type Location view model factory.
 * generate locationviewmodel objects
 */
public class LocationViewModelFactory implements ViewModelProvider.Factory {
    private String bookID;

    /**
     * Instantiates a new Location view model factory.
     *
     * @param bookID the book id
     */
    public LocationViewModelFactory(String bookID) {
        this.bookID = bookID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LocationViewModel(bookID);
    }
}
