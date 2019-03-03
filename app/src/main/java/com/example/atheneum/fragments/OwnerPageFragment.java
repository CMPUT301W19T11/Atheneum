package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.OwnerBooksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The Owner page fragment that can be navigated to using the hamburger menu on the main pages
 * after user has logged in.
 *
 * See: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
 */
public class OwnerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private RecyclerView ownerBooksRecyclerView;
    private RecyclerView.Adapter ownerBooksRecyclerAdapter;
    private RecyclerView.LayoutManager ownerBooksLayoutManager;

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

        ownerBooksRecyclerView = (RecyclerView) this.view.findViewById(R.id.owner_books_recycler_view);
        ownerBooksRecyclerView.setHasFixedSize(true);
        ownerBooksLayoutManager = new LinearLayoutManager(this.context);
        ownerBooksRecyclerView.setLayoutManager(ownerBooksLayoutManager);
        ownerBooksRecyclerAdapter = new OwnerBooksAdapter(ownerBooks);
        ownerBooksRecyclerView.setAdapter(ownerBooksRecyclerAdapter);
        ownerBooksRecyclerView.addItemDecoration(new DividerItemDecoration(ownerBooksRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) this.view.findViewById(R.id.add_book);
        //TODO: go to add book activity
        //fab.setOnClickListener();

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
                        book.setIsbn(data.child(getString(R.string.db_book_isbn))
                                .getValue() != null ?
                                (long)data.child(getString(R.string.db_book_isbn))
                                        .getValue() : null);
                        book.setTitle(data.child(getString(R.string.db_book_title))
                                .getValue() != null ?
                                (String)data.child(getString(R.string.db_book_title))
                                        .getValue() : null);
                        book.setDescription(data.child(getString(R.string.db_book_description))
                                .getValue() != null ?
                                (String)data.child(getString(R.string.db_book_description))
                                        .getValue() : null);
                        book.setAuthor(data.child(getString(R.string.db_book_author))
                                .getValue() != null ?
                                (String)data.child(getString(R.string.db_book_author))
                                        .getValue() : null);
                        //book.setOwner(data.child("owner").getValue() != null ?
                        //        (User)data.child("owner").getValue() : null);
                        //book.setBorrower(data.child("borrower").getValue() != null ?
                        //        (User)data.child("borrower").getValue() : null);
                        book.setStatus(data.child(getString(R.string.db_book_status))
                                .getValue() != null ?
                                Book.Status.valueOf(
                                        (String)data.child(getString(R.string.db_book_status))
                                                .getValue()) : null);
                        book.setRequests(data.child(getString(R.string.db_book_requests))
                                .getValue() != null ?
                                (ArrayList<Request>)data.child(getString(R.string.db_book_requests))
                                        .getValue() : null);
                        long leastSigBits = data.child(getString(R.string.db_book_bookID))
                                .child(getString(R.string.db_book_bookID_leastSigBits))
                                .getValue() != null ? (long)data.child(
                                        getString(R.string.db_book_bookID))
                                .child(getString(R.string.db_book_bookID_leastSigBits))
                                .getValue() : null;
                        long mostSigBits = data.child(getString(R.string.db_book_bookID))
                                .child(getString(R.string.db_book_bookID_mostSigBits))
                                .getValue() != null ? (long)data.child(
                                getString(R.string.db_book_bookID))
                                .child(getString(R.string.db_book_bookID_mostSigBits))
                                .getValue() : null;
                        UUID bookID = new UUID(mostSigBits, leastSigBits);
                        book.setBookID(bookID);
                        book.setPhotos(data.child(getString(R.string.db_book_photos))
                                .getValue() != null ?
                                (ArrayList<String>)data.child(getString(R.string.db_book_photos))
                                        .getValue() : null);
                        ownerBooks.add(book);

                        System.out.println(ownerBooks);
                    }

                    ownerBooksRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }
}
