package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts operations on a list of users retrieved from a Firebase query and provides a LiveData stream
 * to a View (either an Activity or Fragment). This livedata stream can be observed for changes by
 * adding a listener and then updating the view based on the changes to the data. The list of users
 * queried by Firebase is found by users starting with the specified partial user name.
 */
public class SearchUsersViewModel extends ViewModel {
    private static final String TAG = SearchUsersViewModel.class.getSimpleName();

    private FirebaseQueryLiveData queryLiveData;
    private LiveData<List<User>> userListLiveData;

    /**
     * Create a new instance of SearchUsersViewModel
     */
    public SearchUsersViewModel() {
        // Initially set the userNameQuery to search for all userNames starting with the empty string
        // and ending with a very high Unicode character in order to match all user names in the database
        queryLiveData = new FirebaseQueryLiveData(UsersRefUtils.getUserNameQuery(""));
        userListLiveData = Transformations.map(queryLiveData, new Function<DataSnapshot, List<User>>() {
            @Override
            public List<User> apply(DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<User>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    userList.add(data.getValue(User.class));
                }
                return userList;
            }
        });
    }

    /**
     * Updates the query used to search for user names starting with partialUserName.
     *
     * @param partialUserName New query string to pass to Firebase
     */
    public void setUserNameQuery(String partialUserName) {
        Log.i(TAG, "in setUserNameQuery");
        Query query = UsersRefUtils.getUserNameQuery(partialUserName);
        // Updating the query will cause a change in queryLiveData which will then update
        // userListLiveData
        queryLiveData.updateQuery(query);
    }

    /**
     * @return Lifecycle-aware observable stream of {@code List<User>} that the view can observe
     *         for changes.
     */
    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }
}
