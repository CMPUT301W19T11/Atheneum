package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * The type Book info view model factory.
 * generate BookInfoViewModel objects
 */
public class BookInfoViewModelFactory implements ViewModelProvider.Factory {
    private final String bookID;

    /**
     * Instantiates a new Book info view model factory.
     *
     * @param bookID the book id
     */
    public BookInfoViewModelFactory(String bookID) {
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
        return (T) new BookInfoViewModel(bookID);
    }
}
