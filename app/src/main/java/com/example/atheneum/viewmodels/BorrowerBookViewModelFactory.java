package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Factory that instantiates a OwnerBooksViewModel with a particular userID.
 * Read the StackOverflow post linked here to understand we can't instantiate OwnerBooksViewModel directly.
 *
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class BorrowerBookViewModelFactory implements ViewModelProvider.Factory {
    private final String borrowerID;

    /**
     * Create a new instance of OwnerBooksViewModelFactory
     *
     * @param borrowerID User ID of the borrower.
     */
    public BorrowerBookViewModelFactory(String borrowerID) {
        this.borrowerID = borrowerID;
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
        return (T) new OwnerBooksViewModel(borrowerID);
    }
}
