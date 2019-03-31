package com.example.atheneum.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.activities.NewRequestActivity;

import com.example.atheneum.activities.ShowRequestInfoActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.RequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;

/**
 * The Borrower page fragment to list borrower's requested books.
 */
public class BorrowerRequestsFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;
    private FloatingActionButton addRequest;
    private ListView requestView;
    private Spinner requestSpinner;

    private static ArrayList<Pair<Book, String>> requestList = new ArrayList<Pair<Book, String>>();
    private RequestAdapter RequestAdapter;
    private ArrayAdapter<String> requestSpinnerAdapter;
    private User borrower;
    private static final String TAG = "ShowRequest";
    private static String rStatus = "ALL";
    FirebaseUser currentUser;
    /**
     * The Book object borrowed.
     */
    Book book;
    private Intent requestInfoIndent;



    /**
     * Instantiates a new Borrower page fragment.
     */
    public BorrowerRequestsFragment() {
        // required empty constructor
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "Current visibility is onStart");
        this.view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "Current visibility is onResume");

        this.view.setVisibility(View.VISIBLE);
        retriveRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_borrower_requests, container, false);


        this.context = getContext();
        requestView = (ListView) this.view.findViewById(R.id.requestView);

        //https://developer.android.com/guide/topics/ui/controls/spinner
        requestSpinner = (Spinner) this.view.findViewById(R.id.ownBookSpinner);
        requestSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.requestSpinnerArray));
        requestSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestSpinner.setAdapter(requestSpinnerAdapter);


        requestInfoIndent = new Intent(getActivity(), ShowRequestInfoActivity.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (getActivity() instanceof  MainActivity) {
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
                 rStatus = (String) arg0.getSelectedItem().toString();
                Log.d(TAG, "use Spinner "+rStatus);
                retriveRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg1)
            {
                Log.d(TAG,"Nothing Selected");

            }
        });


        RequestAdapter = new RequestAdapter(BorrowerRequestsFragment.this.context, R.layout.request_list_item, requestList);
        requestView.setAdapter(RequestAdapter);

        requestView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pair listItemPair = (Pair) requestView.getItemAtPosition(position);
                Book listItem = (Book) listItemPair.first;

                requestInfoIndent.putExtra("bookID", listItem.getBookID());

                Log.d(TAG, "find requested book1 " + listItem.getBookID());
                requestInfoIndent.putExtra("rStatus", (String) listItemPair.second);
                startActivity(requestInfoIndent);


            }
        });
//        requestList.clear();
//        RequestAdapter.notifyDataSetChanged();




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

    public void retriveRequest(){
        /**
         * Retrieve request list
         */


        /**
         * Get the request list
         */
        if(this.view !=null && this.view.getGlobalVisibleRect(new Rect())) {

            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            final DatabaseReference ref = db.getReference().child(getString(R.string.db_requestCollection))
                    .child(currentUser.getUid());


            Log.d(TAG, "Current visibility is "+ String.valueOf(this.view.getGlobalVisibleRect(new Rect())));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Current visibility 1 is "+ String.valueOf(view.getGlobalVisibleRect(new Rect())));
                    requestList.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {


                        String bookID = item.child(getString(R.string.db_book_bookID)).getValue(String.class);
                        final String Status = item.child(getString(R.string.db_book_request_status)).getValue(String.class);

                        DatabaseReference ref_book = db.getReference().child("books").child(bookID);
                        ref_book.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    book = dataSnapshot.getValue(Book.class);
                                    Log.d(TAG, "find book " + book.getTitle());
                                    Log.d(TAG, "find book with rStatus " + rStatus);
                                    Pair newPair = (new Pair(book, Status));
                                    if (!requestList.contains(newPair)) {
                                        if (rStatus.equals("ALL")) {
                                            requestList.add(newPair);


                                        } else if (rStatus.equals(Status)) {
                                            requestList.add(newPair);
                                        }
                                    }
                                    RequestAdapter.notifyDataSetChanged();

//                                requestList.clear();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        Log.d(TAG, "find request size of list "+Integer.toString(requestList.size()));

    }


}
