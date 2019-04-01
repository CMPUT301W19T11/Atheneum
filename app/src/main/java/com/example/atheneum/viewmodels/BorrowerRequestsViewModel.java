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
 * The type Borrower requests view model.
 * view model for handling requests made by borrowers
 */
public class BorrowerRequestsViewModel extends ViewModel {
    private final String TAG = BorrowerRequestsViewModel.class.getSimpleName();

    private final String userID;
    private final FirebaseQueryLiveData queryLiveData;

    private final LiveData<List<Request>> borrowerRequestLiveData;

    /**
     * Instantiates a new Borrower requests view model.
     *
     * @param userID the user id
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
     * Gets borrower request live data.
     *
     * @return the borrower request live data
     */
    public LiveData<List<Request>> getBorrowerRequestLiveData() {
        return borrowerRequestLiveData;
    }

    /**
     * Filter requests by status list.
     *
     * @param status the status
     * @return the list
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
