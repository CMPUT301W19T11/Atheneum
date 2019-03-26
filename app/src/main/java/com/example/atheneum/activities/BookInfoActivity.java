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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.utils.GoodreadsReviewAdapter;
import com.example.atheneum.models.GoodreadsReviewInfo;
import com.example.atheneum.models.SingletonRequestQueue;
import com.example.atheneum.models.Notification;

import com.example.atheneum.models.Transaction;

import com.example.atheneum.models.User;
import com.example.atheneum.utils.BookRequestViewHolder;
import com.example.atheneum.utils.ConnectionChecker;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.RequestCollectionRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.example.atheneum.viewmodels.TransactionViewModel;
import com.example.atheneum.viewmodels.TransactionViewModelFactory;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * Activity for viewing the specifics of a book.
 * Provides the UI fields and buttons for deleting and editing a book.
 */
public class BookInfoActivity extends AppCompatActivity {
    private Context ctx;

    String title;
    String author;
    long isbn;
    String desc;
    private String bookID;
    private String borrowerID;
    private Book bOok;
    private String status;
    private BookInfoViewModel bookInfoViewModel;
    private TransactionViewModel transactionViewModel;

    private User loggedInUser;
    private DatabaseReference loggedInUserRef;
    private ValueEventListener loggedInUserFirebaseListener;

    private TextView textTitle;
    private TextView textAuthor;
    private TextView textIsbn;
    private TextView textDesc;
    private TextView textStatus;

    private RecyclerView requestsRecyclerView;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView.LayoutManager requestsLayoutManager;

    // temporary initialization
    private GoodreadsReviewInfo goodreadsReviewInfo =
            new GoodreadsReviewInfo(Book.INVALILD_ISBN, GoodreadsReviewInfo.INVALID_RATING,null);
    private RatingBar goodreadsAvgRatingbar;
    private Button getReviewsBtn;


    private Button deleteBtn;
    private Button editBtn;
    private Button scanBtn;

    private LinearLayout borrowerProfileArea;

    private static final String TAG = BookInfoActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "Return from scan ISBN");
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null && bOok != null){
                    Barcode barcode = data.getParcelableExtra("Barcode");
                    String barcodeISBN = String.valueOf(barcode.displayValue);

                    if(barcodeISBN.equals(Long.toString(bOok.getIsbn()))) {
                        Log.i(TAG, "They do equal");
//                        Log.i(TAG, "value of bOokID:" + bOok.getBookID());
//                        Log.i(TAG, "value of ownerID:" + bOok.getOwnerID());
//                        Log.i(TAG, "value of borrowerID:" + bOok.getBorrowerID());



                        TransactionViewModelFactory factory = new TransactionViewModelFactory(bOok.getBookID());
                        transactionViewModel = ViewModelProviders.of(BookInfoActivity.this, factory).get(TransactionViewModel.class);
                        final LiveData<Transaction> transactionLiveData = transactionViewModel.getTransactionLiveData();

                        transactionLiveData.observe(this, new Observer<Transaction>() {
                            @Override
                            public void onChanged(@Nullable Transaction transaction) {
                                if (transaction != null) {
                                    Log.i(TAG, "updateTransaction(): got transaction" + transaction.toString());
                                    Log.i(TAG, "bookID:  "  +  String.valueOf(transaction.getBookID()));
                                    Log.i(TAG, "BScan value:" +  String.valueOf(transaction.getBScan()));
                                    Log.i(TAG, "OScan value:" +  String.valueOf(transaction.getOScan()));
                                    Log.i(TAG, "Owner value:" + transaction.getOwnerID());
                                    Log.i(TAG, "Borrower value:" + transaction.getBorrowerID());
                                    transaction.setOScan(true);
//                                    transaction.setBScan(true);

                                    transaction.setBorrowerID(borrowerID);
                                    transaction.setOwnerID(loggedInUser.getUserID());
                                    transactionViewModel.updateTransaction(transaction);

                                    if(transaction.getComplete()){
                                        if(transaction.getType().equals("CHECKOUT")) {
                                            bOok.setStatus(Book.Status.BORROWED);
                                            DatabaseWriteHelper.updateBook(bOok);
                                            transaction.setType(Transaction.RETURN);
                                            transaction.setComplete(false);
                                            DatabaseWriteHelper.updateTransaction(transaction);
                                        }
                                        else {
                                            bOok.setStatus(Book.Status.AVAILABLE);
                                            bOok.setBorrowerID("");
                                            DatabaseWriteHelper.updateBook(bOok);
                                            DatabaseWriteHelper.deleteTransaction(transaction);
                                        }
                                    }
                                }
                                transactionLiveData.removeObserver(this);
                            }

                        });

                    }

                    else{
                            Toast.makeText(this, "Error: The ISBN does not match that of the requested book",
                                    Toast.LENGTH_SHORT).show();
                        }


                }
                else{
                    Toast.makeText(this, "Error: Barcode not found",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
        else{
            Log.i(TAG, "in else");
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.i(TAG, "leaving onActivityResult");
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        loggedInUserRef.addListenerForSingleValueEvent(loggedInUserFirebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
        loggedInUserRef.removeEventListener(loggedInUserFirebaseListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = this;

        bookID = getIntent().getStringExtra("bookID");
        Log.i("bookid value: ", bookID);
        textTitle = (TextView) findViewById(R.id.bookTitle);
        textAuthor = (TextView) findViewById(R.id.bookAuthor);
        textIsbn = (TextView) findViewById(R.id.bookISBN);
        textDesc = (TextView) findViewById(R.id.bookDescription);
        textStatus = (TextView) findViewById(R.id.bookStatus);



        borrowerProfileArea = (LinearLayout) findViewById(R.id.borrower_prof_area);

        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    bOok = book;

                    textTitle.setText(book.getTitle());
                    textAuthor.setText(book.getAuthor());
                    textIsbn.setText(String.valueOf(book.getIsbn()));
                    textDesc.setText(book.getDescription());
                    textStatus.setText(String.valueOf(book.getStatus()));
                    status = String.valueOf(book.getStatus());

                    if(status.equals("ACCEPTED")  || status.equals("BORROWED")){
                        Log.i(TAG, "scan button visible");
                        scanBtn.setVisibility(View.VISIBLE);
                        scanBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "ISBN scan requested");
                                Intent intent = new Intent(BookInfoActivity.this, ScanBarcodeActivity.class);
                                startActivityForResult(intent, 0);
                            }
                        });
                    }
                    else{
                        Log.i(TAG, "scan button not visible");
                        scanBtn.setVisibility(View.INVISIBLE);
                    }
                    setStatusTextColor(book);
                    borrowerID = book.getBorrowerID();

                    // using borrowerID, show borrower email and profile image, or "None"
                    final TextView borrowerEmailTextView = (TextView) findViewById(R.id.book_borrower_email);
                    final ImageView borrowerPicture = (ImageView) findViewById(R.id.borrower_profile_pic);
                    // retrieve the User's email
                    if (!(borrowerID == null || borrowerID.equals(""))) {
                        Log.i(TAG, "Valid borrowerID");

                        // retrieve email
                        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(borrowerID);
                        UserViewModel userViewModel = ViewModelProviders.of(BookInfoActivity.this, userViewModelFactory).get(UserViewModel.class);
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

                        // set clickable profile area
                        borrowerProfileArea.setClickable(true);
                        borrowerProfileArea.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "Borrower Profile clicked");
                                viewUserProfile(borrowerID);
                            }
                        });

                    }
                    else { // no borrower;
                        Log.i(TAG, "No borrower");
                        borrowerEmailTextView.setText("None");
                        borrowerPicture.setVisibility(View.INVISIBLE);  // hide profile picture placeholder when no borrower
                        borrowerProfileArea.setClickable(false);
                    }

                }
            }
        });

        //get user
        loggedInUserFirebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loggedInUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        loggedInUserRef = UsersRefUtils.getUsersRef(FirebaseAuthUtils.getCurrentUser().getUid());

        Log.v(TAG, bookID);

        // get list of requesters
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            Query query = RequestCollectionRefUtils.getBookRequestCollectionRef(bookID);

            FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                    .setQuery(query, new SnapshotParser<String>() {
                        //@Nonnull is removed, it doesn't work when introduced for some reason
                        @Override
                        public String parseSnapshot(DataSnapshot snapshot) {
                            return snapshot.getKey();
                        }
                    }).build();

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, BookRequestViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final BookRequestViewHolder holder,
                                                int position, @NonNull final String requesterID) {
                    //Bind Book object to BookViewHolder
                    Log.v(TAG, "BIND VIEW HOLDER " + requesterID);

                    //get user object from requesterID
                    DatabaseReference requesterRef = UsersRefUtils.getUsersRef(requesterID);
                    requesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final User requester = dataSnapshot.getValue(User.class);
                            holder.requesterNameTextView.setText(requester.getUserName());
                            holder.declineRequestImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i(TAG, "decline request button pressed");
                                    declineRequest(requester);
                                }
                            });
                            holder.acceptRequestImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i(TAG, "accept request button pressed");
                                    acceptRequest(requester);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
							Log.e(TAG, databaseError.getMessage());
                        }
                    });

                    // set on click to take you to the user's profile page
                    holder.requestItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewUserProfile(requesterID);
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
            requestsLayoutManager = new LinearLayoutManager(this);
            requestsRecyclerView.setLayoutManager(requestsLayoutManager);
            requestsRecyclerView.setAdapter(firebaseRecyclerAdapter);
            requestsRecyclerView.setNestedScrollingEnabled(false);
            requestsRecyclerView.addItemDecoration(new DividerItemDecoration(requestsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));

        }


        hideGoodreadsReview();
        showGoodreadsReviewError("Loading...\n");
        // TODO deal with goodreads reviews
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                long isbn = book.getIsbn();
                getGoodreadsReviewInfo(isbn);

                getReviewsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoReviewsActivity(goodreadsReviewInfo.getReviews_widget_url());
                    }
                });
//                bookLiveData.removeObserver(this);
            }
        });

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

        scanBtn = (Button) findViewById(R.id.scanISBN);

//        TransactionViewModelFactory factory1 = new TransactionViewModelFactory(bookID);
//        TransactionViewModel transactionViewModel = ViewModelProviders.of(BookInfoActivity.this, factory1).get(TransactionViewModel.class);
//        final LiveData<Transaction> transactionLiveData = transactionViewModel.getTransactionLiveData();
//
//        transactionLiveData.observe(this, new Observer<Transaction>() {
//            @Override
//            public void onChanged(@Nullable Transaction transaction) {
//                if(transaction != null){
//                    Log.i(TAG, "transaction exists");
//                    scanBtn.setVisibility(View.VISIBLE);
//                }
//                else{
//                    Log.i(TAG, "scan button not visible");
//                    scanBtn.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        Log.i (TAG, "))))))))))))((((((" + status);
//        Log.i(TAG, "**********" + textStatus.getText().toString());
//        if(status.equals("ACCEPTED")){
//            Log.i(TAG, "scan button visible");
//            scanBtn.setVisibility(View.VISIBLE);
//            scanBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i(TAG, "ISBN scan requested");
//                    Intent intent = new Intent(BookInfoActivity.this, ScanBarcodeActivity.class);
//                    startActivityForResult(intent, 0);
//                }
//            });
//        }
//        else{
//            Log.i(TAG, "scan button not visible");
//            scanBtn.setVisibility(View.INVISIBLE);
//        }
    }


    /**
     * Start activity for a user's profile
     *
     */
    public void viewUserProfile(String userID) {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra(ViewProfileActivity.USER_ID, userID);
        startActivity(intent);
    }

    /**
     * Start activity for editing the book
     */
    public void editBook(){
        Log.i(TAG, "Edit book button pressed");
        Intent intent = new Intent(this, AddEditBookActivity.class);
//        intent.putExtra("ADD_EDIT_BOOK_MODE", EDIT_BOOK);
        intent.putExtra("BookID", bookID);
        startActivity(intent);
        finish();


    }

    /**
     * Triggers bookInfoViewModel to delete book
     */
    public void deleteBook(){
        Log.i(TAG, "Delete book button pressed");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        if (firebaseUser != null) {
            bookInfoViewModel.deleteBook(firebaseUser.getUid());
            finish();
        }
    }

    /**
     * Attempt to get information from goodreads
     */
    public void getGoodreadsReviewInfo(long isbn) {
        // show error instead if there is no internet connection
        ConnectionChecker connectionChecker = new ConnectionChecker(this);
        if (!connectionChecker.isNetworkConnected()) {
            hideGoodreadsReview();
            showGoodreadsReviewError("Ratings and reviews unavailable while offline. \n");
            return;
        }

        goodreadsAvgRatingbar = findViewById(R.id.goodreadsAvgRatingBar);
        getReviewsBtn = findViewById(R.id.gotoReviewsBtn);
        String apiRequestURL = null;


        // null check for ISBN
        if (textIsbn.getText() != null && !textIsbn.getText().toString().equals("")) {
            apiRequestURL = "https://www.goodreads.com/book/isbn/" + String.valueOf(isbn) +
                    "?key=" + getString(R.string.GoodreadsAPI);
        }

        if (apiRequestURL != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRequestURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            GoodreadsReviewAdapter reviewAdapter = new GoodreadsReviewAdapter(response);
                            goodreadsReviewInfo = reviewAdapter.getReviewInfo();

                            hideGoodreadsReviewError();
                            showGoodreadsReview();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "API Response error");
                            hideGoodreadsReview();
                            showGoodreadsReviewError("Couldn't retrieve ratings and reviews from Goodreads for the given ISBN.\n");
                        }
                    });

            SingletonRequestQueue.getInstance(this).getRequestQueue().add(stringRequest);

        }
    }

    private void hideGoodreadsReviewError() {
        // hide the error textview
        TextView goodreadsErrorTextView = findViewById(R.id.goodreads_unavailable_textview);
        goodreadsErrorTextView.setVisibility(View.GONE);
    }

    private void showGoodreadsReviewError(String errorMessage) {
        // show the error textview
        TextView goodreadsErrorTextView = findViewById(R.id.goodreads_unavailable_textview);
        goodreadsErrorTextView.setText(errorMessage);
        goodreadsErrorTextView.setVisibility(View.VISIBLE);
    }

    private void hideGoodreadsReview() {
        // hide the rating bar and reviews button
        goodreadsAvgRatingbar = findViewById(R.id.goodreadsAvgRatingBar);
        goodreadsAvgRatingbar.setVisibility(View.GONE);
        getReviewsBtn = findViewById(R.id.gotoReviewsBtn);
        getReviewsBtn.setVisibility(View.GONE);
    }

    private void showGoodreadsReview() {
        Log.i(TAG, "Rating: " + goodreadsReviewInfo.getAvg_rating());
        // show the rating bar and reviews button
        goodreadsAvgRatingbar = findViewById(R.id.goodreadsAvgRatingBar);
        goodreadsAvgRatingbar.setVisibility(View.VISIBLE);
        getReviewsBtn = findViewById(R.id.gotoReviewsBtn);
        getReviewsBtn.setVisibility(View.VISIBLE);

        if (goodreadsReviewInfo != null){
            goodreadsAvgRatingbar.setRating(goodreadsReviewInfo.getAvg_rating());
        }
    }

    private void gotoReviewsActivity(String widgetURL) {
        Intent intent = new Intent(this, GoodreadsReviewsActivity.class);

        intent.putExtra(GoodreadsReviewsActivity.WEBVIEW_URL, widgetURL);
        startActivity(intent);
    }

    /**
     * Set color of TextView for book status
     *
     * @param book
     */
    public void setStatusTextColor(@Nullable Book book) {
        Book.Status bkStatus = book.getStatus();
        if (bkStatus == Book.Status.ACCEPTED) {
            textStatus.setTextColor(getResources().getColor(R.color.bookAccepted));
        } else if (bkStatus == Book.Status.AVAILABLE) {
            textStatus.setTextColor(getResources().getColor(R.color.bookAvailable));
        } else if (bkStatus == Book.Status.REQUESTED) {
            textStatus.setTextColor(getResources().getColor(R.color.bookRequested));
        } else if (bkStatus == Book.Status.BORROWED) {
            textStatus.setTextColor(getResources().getColor(R.color.bookBorrowed));
        }
    }

    /**
     * Decline request of single requester
     *
     * @param requester
     */
    public void declineRequest(User requester) {
        Notification notification = new Notification(
                requester.getUserID(),
                FirebaseAuthUtils.getCurrentUser().getUid(),
                requester.getUserID(),
                bookID,
                Notification.NotificationType.DECLINE,
                ""
        );
        notification.constructMessage(loggedInUser.getUserName(),
                bookInfoViewModel.getBookLiveData().getValue().getTitle());
        DatabaseWriteHelper.declineRequest(requester.getUserID(), bookID, notification, true);
    }

    /**
     * Accept request for one person, decline for everyone else
     *
     * @param requester
     */
    public void acceptRequest(User requester) {
        Notification acceptNotification = new Notification(
                requester.getUserID(),
                FirebaseAuthUtils.getCurrentUser().getUid(),
                requester.getUserID(),
                bookID,
                Notification.NotificationType.ACCEPT,
                ""
        );
        acceptNotification.constructMessage(loggedInUser.getUserName(),
                bookInfoViewModel.getBookLiveData().getValue().getTitle());
        Notification declineNotification = new Notification(
                "",
                FirebaseAuthUtils.getCurrentUser().getUid(),
                "",
                bookID,
                Notification.NotificationType.DECLINE,
                ""
        );
        declineNotification.constructMessage(loggedInUser.getUserName(),
                bookInfoViewModel.getBookLiveData().getValue().getTitle());
        com.example.atheneum.models.Request request = new com.example.atheneum.models.Request(requester.getUserID(), bookID);
        request.setrStatus(com.example.atheneum.models.Request.Status.ACCEPTED);
        DatabaseWriteHelper.acceptRequest(request, acceptNotification, declineNotification);

        Transaction transaction = new Transaction(Transaction.CHECKOUT, null, requester.getUserID(), loggedInUser.getUserID(), bookID, false, false);
        DatabaseWriteHelper.addNewTransaction(transaction);
    }
}
