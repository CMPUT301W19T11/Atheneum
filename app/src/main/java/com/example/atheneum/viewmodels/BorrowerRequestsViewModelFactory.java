package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Factory that instantiates a BorrowerRequestsViewModelFactory with a particular userID.
 * Read the StackOverflow post linked here to understand we can't instantiate BorrowerRequestsViewModel directly.
 *
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class BorrowerRequestsViewModelFactory implements ViewModelProvider.Factory {
    private String userID;

    /**
     * Instantiate a new instance of BorrowerRequestsViewModelFactory
     *
     * @param userID User ID of the user who wants to see their requests
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
