package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class newRequest extends Fragment {

    private User requester;
    private EditText bookIDTest;
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private Button savebtn;
    private static final String TAG = "AddRequest";

    public newRequest() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.new_request, container, false);

        this.context = getContext();

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));

        }

        bookIDTest = this.view.findViewById(R.id.bookIDTest);

        savebtn = this.view.findViewById(R.id.save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference()
                        .child(getString(R.string.db_users)).child(currentUser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            requester = dataSnapshot.getValue(User.class);
                            String bookID = bookIDTest.getText().toString();
                            Request newRequest = new Request(requester, bookID);
                            // add book to the owner's collection
                            Log.i(TAG, "send added, id=" + newRequest.getBookID());

//                            DatabaseReference requestColref = db.getReference()
//                                    .child(getString(R.string.db_requestCollection)).child(newRequest.getRequesterID());
//                            requestColref.child(newRequest.getBookID()).setValue(newRequest);

                            DatabaseWriteHelper.makeRequest(newRequest);

                            Log.i(TAG, "Request added, id=" + newRequest.getBookID());

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .remove(newRequest.this).commit();

                            // return to owner page fragment
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, new BorrowerPageFragment()).commit();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return this.view;
    }

}
