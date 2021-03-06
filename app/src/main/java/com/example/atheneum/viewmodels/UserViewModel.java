package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Abstracts operations on a user retrieved from a Firebase query and provides a LiveData stream
 * to a View (either an Activity or Fragment). This livedata stream can be observed for changes by
 * adding a listener and then updating the view based on the changes to the data.
 * <p>
 * Since we can't call {@code ViewModelProviders.of(lifeCycleOwner).get(UserViewModel.class)} to get
 * a UserViewModel with a userID passed in, we have to use the UserViewModelFactory to instantiate
 * the UserViewModel: {@code ViewModelProviders.of(lifeCycleOwner, new UserViewModelFactory(userID)).get(UserViewModel.class) }
 * <p>
 * lifecyleOwner refers to either an Activity or a Fragment.
 * <p>
 * See: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components_20.html
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components_22.html
 */
public class UserViewModel extends ViewModel {
    // Raw stream of read-only DataSnapshot values retrieved from query
    private final FirebaseQueryLiveData queryLiveData;
    // Serialized user data to sent to view
    private final LiveData<User> userLiveData;
    // Reference to user in Firebase
    private final DatabaseReference userRef;

    /**
     * Generate view model provider key string.
     *
     * @param userID the user id
     * @return the string
     */
    public static String generateViewModelProviderKey(String userID) {
        return UserViewModel.class.getCanonicalName() + ":" + userID;
    }

    /**
     * Creates an instance of UserViewModel that watches for changes to data held in a Firebase
     * Query, transforms it to a User object, and allows a view to observer for changes on this
     * data in order to update it's UI elements.
     *
     * @param userID User ID of user queried in the database
     */
    public UserViewModel(String userID) {
        userRef = UsersRefUtils.getUsersRef(userID);
        queryLiveData = new FirebaseQueryLiveData(userRef);
        userLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    /**
     * Converts the DataSnapshot retrieved from the Firebase Query into a User object
     */
    private class Deserializer implements Function<DataSnapshot, User> {
        @Override
        public User apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(User.class);
        }
    }

    /**
     * Updates the user information in the database
     *
     * @param user New user value
     */
    public void setUser(User user) {
        userRef.setValue(user);
    }

    /**
     * Gets user live data.
     *
     * @return Observable User data from Firebase
     */
    @NonNull
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
}
