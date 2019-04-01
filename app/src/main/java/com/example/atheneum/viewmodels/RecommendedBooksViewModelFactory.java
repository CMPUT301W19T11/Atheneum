package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Book;

/**
 * Factory that instantiates a RecommendedBooksViewModelFactory with a particular borrowerID and isbn.
 * Read the StackOverflow post linked here to understand we can't instantiate RecommendedBooksViewModel directly.
 * <p>
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class RecommendedBooksViewModelFactory implements ViewModelProvider.Factory {
    private final String borrowerID;
    private final Long isbn;

    /**
     * Create a new instance of RecommendedBooksViewModelFactory
     *
     * @param borrowerID User ID of the borrower who just returned a book.
     * @param isbn       ISBN of the newly returned book.
     */
    public RecommendedBooksViewModelFactory(String borrowerID, Long isbn) {
        this.borrowerID = borrowerID;
        this.isbn = isbn;
    }

    /**
     * Create a new instance of RecommendedBooksViewModelFactory
     *
     * @param borrowerID User ID of the borrower who just returned a book.
     */
    public RecommendedBooksViewModelFactory(String borrowerID) {
        this.borrowerID = borrowerID;
        this.isbn = Book.INVALILD_ISBN;
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
        return (T) new RecommendedBooksViewModel(borrowerID, isbn);
    }
}
