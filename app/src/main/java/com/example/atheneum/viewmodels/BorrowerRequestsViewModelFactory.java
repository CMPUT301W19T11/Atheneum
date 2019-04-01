package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * The type Borrower requests view model factory.
 * generate BorrowerRequestsViewModel objects
 */
public class BorrowerRequestsViewModelFactory implements ViewModelProvider.Factory {
    private String userID;

    /**
     * Instantiates a new Borrower requests view model factory.
     *
     * @param userID the user id
     */
    public BorrowerRequestsViewModelFactory(String userID) {
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
        return (T) new BorrowerRequestsViewModel(userID);
    }
}
