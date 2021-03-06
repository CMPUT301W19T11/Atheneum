package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * The type Add book view model factory.
 * generates addbookviewmodel objects
 */
public class AddBookViewModelFactory implements ViewModelProvider.Factory {
    private final String userID;

    /**
     * Instantiates a new Add book view model factory.
     *
     * @param userID the user id
     */
    public AddBookViewModelFactory(String userID) {
        this.userID = userID;
    }

    /**
     * Creates a new instance of the given {@code Class}.
     * <p>
     *
     * @param modelClass a {@code Class} whose instance is requested
     * @return a newly created ViewModel
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddBookViewModel(userID);
    }
}
