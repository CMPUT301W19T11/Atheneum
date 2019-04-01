package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class LocationViewModelFactory implements ViewModelProvider.Factory {
    private String bookID;

    public LocationViewModelFactory(String bookID) {
        this.bookID = bookID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LocationViewModel(bookID);
    }
}
