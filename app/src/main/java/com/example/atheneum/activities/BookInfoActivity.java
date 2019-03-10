/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.activities;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.BookRequestViewHolder;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.RequestCollectionRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookInfoActivity extends AppCompatActivity {

    String title;
    String author;
    long isbn;
    String desc;
    private String bookID;
    private String borrowerID;
    private BookInfoViewModel bookInfoViewModel;
    private UserViewModel userViewModel;

    private TextView textTitle;
    private TextView textAuthor;
    private TextView textIsbn;
    private TextView textDesc;
    private TextView textStatus;

    private RecyclerView requestsRecyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView.LayoutManager requestsLayoutManager;

    private Button deleteBtn;
    private Button editBtn;

    private static final String TAG = BookInfoActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bookID = getIntent().getStringExtra("bookID");
        Log.i("bookid value: ", bookID);
        textTitle = (TextView) findViewById(R.id.bookTitle);
        textAuthor = (TextView) findViewById(R.id.bookAuthor);
        textIsbn = (TextView) findViewById(R.id.bookISBN);
        textDesc = (TextView) findViewById(R.id.bookDescription);
        textStatus = (TextView) findViewById(R.id.bookStatus);

        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    textTitle.setText(book.getTitle());
                    textAuthor.setText(book.getAuthor());
                    textIsbn.setText(String.valueOf(book.getIsbn()));
                    textDesc.setText(book.getDescription());
                    textStatus.setText(String.valueOf(book.getStatus()));
                    borrowerID = book.getBorrowerID();

                    // using borrowerID, show borrower email and profile image, or "None"
                    final TextView borrowerEmailTextView = (TextView) findViewById(R.id.book_borrower_email);
                    final ImageView borrowerPicture = (ImageView) findViewById(R.id.borrower_profile_pic);
                    // retrieve the User's email
                    if (!(borrowerID == null || borrowerID.equals(""))) {
                        Log.i(TAG, "Valid borrowerID");

                        // retrieve email
                        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(borrowerID);
                        userViewModel = ViewModelProviders.of(BookInfoActivity.this, userViewModelFactory).get(UserViewModel.class);
                        final LiveData<User> userLiveData = userViewModel.getUserLiveData();

                        userLiveData.observe(BookInfoActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(@Nullable User user) {
                                Log.i(TAG, "in Observer!");
                                // update borrower email
                                borrowerEmailTextView.setText(user.getUserName());
                                Log.i(TAG, "User email*** " + user.getUserName());
                                if (user.getPhotos().size() > 0) {
                                    String userPic = user.getPhotos().get(0);
                                    Bitmap bitmapPhoto = PhotoUtils.DecodeBase64BitmapPhoto(userPic);
                                    borrowerPicture.setImageBitmap(bitmapPhoto);
                                }
                                else {
                                    borrowerPicture.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));
                                }
                                borrowerPicture.setVisibility(View.VISIBLE);
                                // show image
                                // Remove the observer after update
                                userLiveData.removeObserver(this);
                            }
                        });
                    }
                    else { // no borrower;
                        Log.i(TAG, "No borrower");
                        borrowerEmailTextView.setText("None");
                        borrowerPicture.setVisibility(View.INVISIBLE);  // hide profile picture placeholder when no borrower
                    }
                }
            }
        });


        Log.v(TAG, bookID);

        // get list of requesters
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            Query query = RequestCollectionRefUtils.getBookRequestCollectionRef(bookID);

            FirebaseRecyclerOptions<String> options =
                    new FirebaseRecyclerOptions.Builder<String>()
                            .setQuery(query, new SnapshotParser<String>() {
                                //@Nonnull is removed, it doesn't work when introduced for some reason
                                @Override
                                public String parseSnapshot(DataSnapshot snapshot) {
                                    return snapshot.getKey();
                                }
                            }).build();

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, BookRequestViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final BookRequestViewHolder holder, int position, @NonNull final String requesterID) {
                    //Bind Book object to BookViewHolder
                    Log.v(TAG, "BIND VIEW HOLDER " + requesterID);

                    //get user object from requesterID
                    DatabaseReference requesterRef = UsersRefUtils.getUsersRef(requesterID);
                    requesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User requester = dataSnapshot.getValue(User.class);
                            holder.requesterNameTextView.setText(requester.getUserName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
							Log.e(TAG, databaseError.getMessage());
                        }
                    });
                }

                @Override
                public BookRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    // create a new view
                    LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.request_on_book_card, parent, false);
                    final BookRequestViewHolder vh = new BookRequestViewHolder(v);

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
            };

            requestsRecyclerView = (RecyclerView) findViewById(R.id.book_requests_recycler_view);
            requestsRecyclerView.setHasFixedSize(true);
            requestsLayoutManager = new LinearLayoutManager(this);
            requestsRecyclerView.setLayoutManager(requestsLayoutManager);
            requestsRecyclerView.setAdapter(firebaseRecyclerAdapter);
            requestsRecyclerView.addItemDecoration(new DividerItemDecoration(requestsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
        }

        deleteBtn = findViewById(R.id.buttonDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });

        editBtn = findViewById(R.id.buttonEdit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBook();
            }
        });
    }

    public void editBook(){
        Log.i(TAG, "Edit book button pressed");
        Intent intent = new Intent(this, AddEditBookActivity.class);
//        intent.putExtra("ADD_EDIT_BOOK_MODE", EDIT_BOOK);
        intent.putExtra("BookID", bookID);
        startActivity(intent);
        finish();


    }

    public void deleteBook(){
        Log.i(TAG, "Delete book button pressed");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        if (firebaseUser != null) {
            bookInfoViewModel.deleteBook(firebaseUser.getUid());
            finish();
        }
    }

}
