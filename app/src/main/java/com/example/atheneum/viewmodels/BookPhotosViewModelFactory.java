package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Factory that instantiates a BookPhotosViewModel with a particular bookID.
 * Read the StackOverflow post linked here to understand we can't instantiate BookPhotosViewModel directly.
 * <p>
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class BookPhotosViewModelFactory implements ViewModelProvider.Factory {
    private final String bookID;

    /**
     * Create a new instance of BookPhotosViewModelFactory
     *
     * @param bookID Book ID of book of interest
     */
    public BookPhotosViewModelFactory(String bookID) {
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
        return (T) new BookPhotosViewModel(bookID);
    }
}
