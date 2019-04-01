package com.example.atheneum.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Factory that instantiates a UserViewModel with a particular userID.
 * Read the StackOverflow post linked here to understand we can't instantiate UserViewModel directly.
 * <p>
 * See: https://stackoverflow.com/a/46704702/11039833
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final String userID;

    /**
     * Instantiates a new User view model factory.
     *
     * @param userID the user id
     */
    public UserViewModelFactory(String userID) {
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
        return (T) new UserViewModel(userID);
    }
}
