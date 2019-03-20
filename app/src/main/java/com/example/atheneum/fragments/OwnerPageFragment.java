package com.example.atheneum.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.atheneum.R;
import com.example.atheneum.activities.AddEditBookActivity;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.BookViewHolder;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.OwnerBooksAdapter;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.OwnerCollectionRefUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * The Owner page fragment that can be navigated to using the hamburger menu on the main pages
 * after user has logged in.
 *
 * See: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
 * See: https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
 * See: https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
 */
public class OwnerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private RecyclerView ownerBooksRecyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView.LayoutManager ownerBooksLayoutManager;

    private UserViewModel userViewModel;

    private static final String TAG = OwnerPageFragment.class.getSimpleName();

    public static final int REQUEST_DELETE_ENTRY = 1;


    /**
     * Instantiates a new Owner page fragment.
     */
    public OwnerPageFragment() {
        // required empty constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
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

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Query keyQuery = OwnerCollectionRefUtils.getOwnerCollectionRef(firebaseUser.getUid());
            DatabaseReference dataRef = BooksRefUtils.BOOKS_REF;

            FirebaseRecyclerOptions<Book> options =
                    new FirebaseRecyclerOptions.Builder<Book>()
                            .setIndexedQuery(keyQuery, dataRef, Book.class)
                            .build();

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final BookViewHolder holder, int position, @NonNull final Book book) {
                    Log.i(TAG, "in onBind!");

                    //Bind Book object to BookViewHolder
                    holder.titleTextView.setText(
                            book.getTitle());
                    holder.authorTextView.setText(
                            book.getAuthor());
                    holder.statusTextView.setText(
                            book.getStatus().toString());

                    // change color of status text
                    setStatusTextColor(holder, book);

                    // retrieve the User's email
                    if (!(book.getBorrowerID() == null || book.getBorrowerID().equals(""))) {
                        Log.i(TAG, "Borrower exists : " + book.getBorrowerID());
                        // retrieve email with direct call to firebase
                        final FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference borrowerRef = db.getReference().child(getString(R.string.db_users)).child(book.getBorrowerID());
                        borrowerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    User borrower = dataSnapshot.getValue(User.class);
                                    holder.borrowerNameTextView.setText(borrower.getUserName());
                                }
                                else {
                                    Log.e(TAG, "nonexistent user error, shouldn't be here");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "User listener was cancelled");
                            }
                        });
                        
                    }
                    else { // no borrower;
                        holder.borrowerNameTextView.setText("None");
                    }

                    holder.bookItem.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v){
                            //                Toast.makeText(parent.getContext(), "Test Click" + String.valueOf(vh.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                            Log.i("OwnerBook", "clicked on a book");
                            String sBookId = book.getBookID();
                            Intent intent = new Intent(context, BookInfoActivity.class);
                            intent.putExtra("bookID", sBookId);
                            intent.putExtra("position", holder.getAdapterPosition());

                            mainActivity.startActivityForResult(intent, REQUEST_DELETE_ENTRY);

                        }
                    });


                }

                @Override
                public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    // create a new view
                    LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.book_card, parent, false);
                    final BookViewHolder vh = new BookViewHolder(v);

                    return vh;
                }

                @Override
                public void onDataChanged() {
                    // Called each time there is a new data snapshot. You may want to use this method
                    // to hide a loading spinner or check for the "no documents" state and update your UI.
                    // ...
                }

                @Override
                public void onError(DatabaseError e) {
                    // Called when there is an error getting data. You may want to update
                    // your UI to display an error message to the user.
                    // ...
                    Log.i(TAG, e.getMessage());
                }

                /**
                 * Set color of TextView for book status
                 *
                 * @param holder
                 * @param book
                 */
                public void setStatusTextColor(@NonNull final BookViewHolder holder, @NonNull final Book book) {
                    Book.Status bkStatus = book.getStatus();
                    if (bkStatus == Book.Status.ACCEPTED) {
                        holder.statusTextView
                                .setTextColor(getResources().getColor(R.color.bookAccepted));
                    } else if (bkStatus == Book.Status.AVAILABLE) {
                        holder.statusTextView
                                .setTextColor(getResources().getColor(R.color.bookAvailable));
                    } else if (bkStatus == Book.Status.REQUESTED) {
                        holder.statusTextView
                                .setTextColor(getResources().getColor(R.color.bookRequested));
                    } else if (bkStatus == Book.Status.BORROWED) {
                        holder.statusTextView
                                .setTextColor(getResources().getColor(R.color.bookBorrowed));
                    }
                }
            };

            ownerBooksRecyclerView = (RecyclerView) this.view.findViewById(R.id.owner_books_recycler_view);
            ownerBooksRecyclerView.setHasFixedSize(true);
            ownerBooksLayoutManager = new LinearLayoutManager(this.context);
            ownerBooksRecyclerView.setLayoutManager(ownerBooksLayoutManager);
            ownerBooksRecyclerView.setAdapter(firebaseRecyclerAdapter);
            ownerBooksRecyclerView.addItemDecoration(new DividerItemDecoration(ownerBooksRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));

            FloatingActionButton fab = (FloatingActionButton) this.view.findViewById(R.id.add_book);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
                    intent.putExtra("BookID", "");
                    startActivity(intent);
                }
            });
        } else {
            Log.w(TAG, "impossible!!!");
        }

        return this.view;
    }
}
