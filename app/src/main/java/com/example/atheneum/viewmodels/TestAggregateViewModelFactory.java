package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class TestAggregateViewModelFactory implements ViewModelProvider.Factory {
    private final String borrowerID;
    private final String bookID;

    public TestAggregateViewModelFactory(String borrowerID, String bookID) {
        this.borrowerID = borrowerID;
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
        return (T) new TestAggregateViewModel(borrowerID, bookID);
    }
}
