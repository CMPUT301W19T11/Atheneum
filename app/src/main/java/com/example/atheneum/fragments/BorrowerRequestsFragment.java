package com.example.atheneum.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.atheneum.R;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.activities.NewRequestActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.viewmodels.BorrowerRequestsViewModel;
import com.example.atheneum.viewmodels.BorrowerRequestsViewModelFactory;
import com.example.atheneum.views.adapters.BorrowerRequestListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * The Borrower page fragment to list borrower's requested books.b
 * See: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
 * See: https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
 * See: https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
 * See: https://developer.android.com/guide/topics/ui/controls/spinner
 * See: https://stackoverflow.com/questions/2399086/how-to-use-spinner
 * See: https://stackoverflow.com/questions/45340096/how-do-i-get-the-spinner-clicked-item-out-of-the-onitemselectedlistener-in-this
 */
public class BorrowerRequestsFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;
    private FloatingActionButton addRequest;
    private RecyclerView requestListRecyclerView;
    private Spinner requestSpinner;

    private ArrayAdapter<String> requestSpinnerAdapter;
    private static final String TAG = BorrowerRequestsFragment.class.getSimpleName();
    private Request.Status requestStatus;

    private static String rStatus = "ALL";
    FirebaseUser currentUser;
    /**
     * The Book object borrowed.
     */
    Book book;

    /**
     * Instantiates a new Borrower page fragment.
     */
    public BorrowerRequestsFragment() {
        // required empty constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Current visibility is onStart");
        this.view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Current visibility is onResume");

        this.view.setVisibility(View.VISIBLE);
//        retriveRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_borrower_requests, container, false);

        this.context = getContext();

        //https://developer.android.com/guide/topics/ui/controls/spinner
        requestSpinner = (Spinner) this.view.findViewById(R.id.ownBookSpinner);
        requestSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.requestSpinnerArray));
        requestSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestSpinner.setAdapter(requestSpinnerAdapter);

        requestListRecyclerView = this.view.findViewById(R.id.request_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        requestListRecyclerView.setLayoutManager(layoutManager);
        requestListRecyclerView.addItemDecoration(new DividerItemDecoration(requestListRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        final BorrowerRequestListAdapter listAdapter = new BorrowerRequestListAdapter();
        listAdapter.setOnRequestItemClickListener(new BorrowerRequestListAdapter.OnRequestItemClickListener() {
            @Override
            public void onClick(View v, Request request) {
                Intent requestBookInfoIntent = new Intent(getActivity(), BookInfoActivity.class);
                Log.d(TAG, "find requested book1 " + request.getBookID());

                requestBookInfoIntent.putExtra(BookInfoActivity.BOOK_ID, request.getBookID());
                requestBookInfoIntent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.REQUSET_VIEW);

                startActivity(requestBookInfoIntent);
            }
        });
        requestListRecyclerView.setAdapter(listAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final BorrowerRequestsViewModel borrowerRequestsViewModel = ViewModelProviders
                .of(this, new BorrowerRequestsViewModelFactory(currentUser.getUid()))
                .get(BorrowerRequestsViewModel.class);
        borrowerRequestsViewModel.getBorrowerRequestLiveData().observe(this, new Observer<List<Request>>() {
            @Override
            public void onChanged(@Nullable List<Request> requests) {
                listAdapter.submitList(borrowerRequestsViewModel.filterRequestsByStatus(requestStatus));
            }
        });

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
//            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));
        }


        //https://stackoverflow.com/questions/2399086/how-to-use-spinner
        //https://stackoverflow.com/questions/45340096/how-do-i-get-the-spinner-clicked-item-out-of-the-onitemselectedlistener-in-this
        this.view.setVisibility(View.GONE);
        requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view1, int pos, long id) {
                String rStatus = (String) arg0.getSelectedItem().toString();
                Log.i(TAG, "use Spinner " + rStatus);
                if (rStatus.equals("ALL")) {
                    requestStatus = null;
                } else {
                    requestStatus = Request.Status.valueOf(rStatus);
                }
                listAdapter.submitList(borrowerRequestsViewModel.filterRequestsByStatus(requestStatus));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg1) {
                Log.d(TAG, "Nothing Selected");

            }
        });

        /**
         * go to request generation activity
         */
        final FragmentManager fragmentManager = getFragmentManager();
        addRequest = this.view.findViewById(R.id.new_request);
        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);

                Intent new_request_intent = new Intent(getActivity(), NewRequestActivity.class);
//                ref.removeEventListener();
                startActivity(new_request_intent);
            }
        });

        return this.view;
    }
}
