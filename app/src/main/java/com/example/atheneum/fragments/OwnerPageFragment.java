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
import com.example.atheneum.models.Request;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * The Owner page fragment that can be navigated to using the hamburger menu on the main pages
 * after user has logged in.
 */
public class OwnerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private ArrayList<Book> ownerBooks = new ArrayList<Book>();

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
     * Retrieve owner books from Firebase.
     */
    public void retrieveBooks() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child(getString(R.string.db_ownerCollection)).
                child(firebaseUser.getUid());

        if (firebaseUser != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //TODO: make ownerBookFirebaseParser class
                    //manually parse every field in book
                    Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                    for (DataSnapshot data : dataSnapshotIterable) {
                        Book book = new Book();
                        book.setIsbn(data.child("isbn").getValue() != null ?
                                (long)data.child("isbn").getValue() : null);
                        book.setTitle(data.child("title").getValue() != null ?
                                (String)data.child("title").getValue() : null);
                        book.setDescription(data.child("description").getValue() != null ?
                                (String)data.child("description").getValue() : null);
                        book.setAuthor(data.child("author").getValue() != null ?
                                (String)data.child("author").getValue() : null);
                        //book.setOwner(data.child("owner").getValue() != null ?
                        //        (User)data.child("owner").getValue() : null);
                        //book.setBorrower(data.child("borrower").getValue() != null ?
                        //        (User)data.child("borrower").getValue() : null);
                        book.setStatus(data.child("status").getValue() != null ?
                                Book.Status.valueOf(
                                        (String)data.child("status").getValue()) : null);
                        book.setRequests(data.child("requests").getValue() != null ?
                                (ArrayList<Request>)data.child("requests").getValue() : null);
                        long leastSigBits = data.child("bookID").child("leastSignificantBits").
                                getValue() != null ? (long)data.child("bookID").
                                child("leastSignificantBits").getValue() : null;
                        long mostSigBits = data.child("bookID").child("mostSignificantBits").
                                getValue() != null ? (long)data.child("bookID").
                                child("mostSignificantBits").getValue() : null;
                        UUID bookID = new UUID(mostSigBits, leastSigBits);
                        book.setBookID(bookID);
                        book.setPhotos(data.child("photos").getValue() != null ?
                                (ArrayList<String>)data.child("photos").getValue() : null);
                        ownerBooks.add(book);

                        System.out.println(ownerBooks);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }
}
