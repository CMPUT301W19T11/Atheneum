package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Factory that instantiates a FirstBookPhotoViewModel with a particular bookID.
 * Read the StackOverflow post linked here to understand we can't instantiate FirstBookPhotoViewModel directly.
 * <p>
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class FirstBookPhotoViewModelFactory implements ViewModelProvider.Factory {
    private final String bookID;

    /**
     * Create a new instance of FirstBookPhotoViewModelFactory.
     *
     * @param bookID Unique identifier for the book to get first picture for.
     */
    public FirstBookPhotoViewModelFactory(String bookID) {
        this.bookID = bookID;
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
        return (T) new FirstBookPhotoViewModel(bookID);
    }
}
