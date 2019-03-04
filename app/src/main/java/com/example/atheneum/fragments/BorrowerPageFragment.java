package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.requestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class BorrowerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;
    private FloatingActionButton addRequest;
    private ListView requestView;

    private static ArrayList<Request> requestList=new ArrayList<Request>();;
    private requestAdapter requestAdapter;
    private User borrower;
    private static final String TAG = "ShowRequest";

    public BorrowerPageFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_borrower_page, container, false);

        this.context = getContext();
        requestView = (ListView) this.view.findViewById(R.id.requestView);

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));
        }

        /**
         * Retrieve request
         */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child(getString(R.string.db_requestCollection)).child(currentUser.getUid());

        /**
         * looping the request list
         */
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Request requestItem = new Request();
                    long leastSigBits = item.child(getString(R.string.db_book_bookID))
                            .child(getString(R.string.db_book_bookID_leastSigBits))
                            .getValue() != null ? (long)item.child(
                            getString(R.string.db_book_bookID))
                            .child(getString(R.string.db_book_bookID_leastSigBits))
                            .getValue() : null;
                    long mostSigBits = item.child(getString(R.string.db_book_bookID))
                            .child(getString(R.string.db_book_bookID_mostSigBits))
                            .getValue() != null ? (long)item.child(
                            getString(R.string.db_book_bookID))
                            .child(getString(R.string.db_book_bookID_mostSigBits))
                            .getValue() : null;
                    UUID requestID = new UUID(mostSigBits, leastSigBits);
                    requestItem.setBookID(requestID);
                    User requester = item.child(getString(R.string.db_book_requester)).getValue(User.class);

                    requestItem.setRequester(requester);
                    Request.Status status = item.child(getString(R.string.db_book_request_status)).getValue(Request.Status.class);
                    requestItem.setrStatus(status);

                    Log.d(TAG, "find request "+requestItem.getBookID().toString());
                    requestList.add(requestItem);
                    requestAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        Log.d(TAG, "find request size of list "+Integer.toString(requestList.size()));
        requestAdapter = new requestAdapter(BorrowerPageFragment.this.context, R.layout.request_list_item, requestList);
        requestView.setAdapter(requestAdapter);

//        ownerBooksRecyclerView = (RecyclerView) this.view.findViewById(R.id.owner_books_recycler_view);
//        ownerBooksRecyclerView.setHasFixedSize(true);
//        ownerBooksLayoutManager = new LinearLayoutManager(this.context);
//        ownerBooksRecyclerView.setLayoutManager(ownerBooksLayoutManager);
//        ownerBooksRecyclerAdapter = new OwnerBooksAdapter(ownerBooks);
//        ownerBooksRecyclerView.setAdapter(ownerBooksRecyclerAdapter);
//        ownerBooksRecyclerView.addItemDecoration(new DividerItemDecoration(ownerBooksRecyclerView.getContext(),
//                DividerItemDecoration.VERTICAL));



        final FragmentManager fragmentManager = getFragmentManager();

        addRequest = this.view.findViewById(R.id.new_request);
        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new newRequest()).commit();
            }
        });

        return this.view;
    }


}
