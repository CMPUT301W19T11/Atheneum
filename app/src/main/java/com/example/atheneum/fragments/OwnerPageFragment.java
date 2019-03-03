package com.example.atheneum.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.OwnerCollection;
import com.example.atheneum.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Owner;

/**
 * The Owner page fragment that can be navigated to using the hamburger menu on the main pages
 * after user has logged in.
 */
public class OwnerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    /**
     * Instantiates a new Owner page fragment.
     */
    public OwnerPageFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_owner_page, container, false);

        this.context = getContext();

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.owner_page_title));
        }

        retrieveBooks();

        return this.view;
    }

    /**
     * Retrieve books from Firebase.
     *
     * See: https://stackoverflow.com/questions/37902635/no-setter-field-for-warning-firebase-database-retrieve-data-populate-listview
     */
    public void retrieveBooks() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        String dbOwnerBooksPath = getString(R.string.db_ownerCollection) + "/" + firebaseUser.getUid().toString();
        DatabaseReference ref = db.getReference(dbOwnerBooksPath);

        if (firebaseUser != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> books = dataSnapshot.getChildren();

                    for (DataSnapshot book : books) {
                        System.out.println(book.getValue(Book.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
