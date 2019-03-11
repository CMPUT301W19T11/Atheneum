package com.example.atheneum.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.activities.NewRequest;

import com.example.atheneum.activities.ViewProfileActivity;
import com.example.atheneum.models.Book;
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

    private static ArrayList<Book> requestList = new ArrayList<Book>();
    private requestAdapter requestAdapter;
    private User borrower;
    private static final String TAG = "ShowRequest";
    Book book;



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


        requestAdapter = new requestAdapter(BorrowerPageFragment.this.context, R.layout.request_list_item, requestList);
        requestView.setAdapter(requestAdapter);
//        requestList.clear();
//        requestAdapter.notifyDataSetChanged();

        /**
         * Retrieve request
         */
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child(getString(R.string.db_requestCollection))
                .child(currentUser.getUid());

        /**
         * Get the request list
         */
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();

                for (DataSnapshot item: dataSnapshot.getChildren()) {


                    String bookID = item.child(getString(R.string.db_book_bookID)).getValue(String.class);

                    DatabaseReference ref_book = db.getReference().child("books").child(bookID);
                    ref_book.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                book = dataSnapshot.getValue(Book.class);
                                Log.d(TAG, "find book " + book.getTitle());
                                if(!requestList.contains(book)){
                                    requestList.add(book);

                                }
                                requestAdapter.notifyDataSetChanged();



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


        Log.d(TAG, "find request size of list "+Integer.toString(requestList.size()));


        /**
         * go to request generation activity 
         */
        final FragmentManager fragmentManager = getFragmentManager();
        addRequest = this.view.findViewById(R.id.new_request);
        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_request_intent = new Intent(getActivity(), NewRequest.class);
                startActivity(new_request_intent);
            }
        });

        return this.view;
    }


}
