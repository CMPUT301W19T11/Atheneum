package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.Transaction;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.RequestCollectionRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstracts operations on a list of borrower requests from a Firebase query and provides
 * a LiveData stream to a View (either an Activity or Fragment). This livedata stream can be observed
 * for changes by adding an observer and then updating the view based on the changes to the data.
 */
public class BorrowerRequestsViewModel extends ViewModel {
    private final String TAG = BorrowerRequestsViewModel.class.getSimpleName();

    private final String userID;
    private final FirebaseQueryLiveData queryLiveData;

    private final LiveData<List<Request>> borrowerRequestLiveData;

    /**
     * Instantiate a new instance of BorrowerRequestsViewModel
     *
     * @param userID User ID of the borrower
     */
    public BorrowerRequestsViewModel(String userID) {
        this.userID = userID;
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref = db.getReference().child("requestCollection").child(userID);

        queryLiveData = new FirebaseQueryLiveData(RequestCollectionRefUtils.getOwnerRequestCollectionRef(userID));
        borrowerRequestLiveData = Transformations.map(queryLiveData, new Function<DataSnapshot, List<Request>>() {
            @Override
            public List<Request> apply(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Request> requestList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        requestList.add(data.getValue(Request.class));
                    }
                    return requestList;
                }
                return null;
            }
        });
    }

    /**
     * @return Lifecycle-aware observable stream of {@code List<Request>} that the view can observe
     * for changes.
     */
    public LiveData<List<Request>> getBorrowerRequestLiveData() {
        return borrowerRequestLiveData;
    }

    /**
     *
     * @param status Status to filter on. If null, shows requests with all statuses.
     * @return Filtered list of requests
     */
    public List<Request> filterRequestsByStatus(Request.Status status) {
        List<Request> requestList = borrowerRequestLiveData.getValue();
        if (requestList != null && status != null) {
            List<Request> filteredRequests = new ArrayList<>();
            for (Request request : requestList) {
                if (request.getrStatus().equals(status)) {
                    filteredRequests.add(request);
                }
            }
            return filteredRequests;
        }
        return requestList;
    }

}
