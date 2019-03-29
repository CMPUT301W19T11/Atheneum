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

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
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
import com.example.atheneum.models.Photo;
//import com.example.atheneum.models.Request;
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
import com.example.atheneum.viewmodels.FirstBookPhotoViewModel;
import com.example.atheneum.viewmodels.FirstBookPhotoViewModelFactory;
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


/**
 * Activity for viewing the specifics of a book.
 * Provides the UI fields and buttons for deleting and editing a book.
 */
public class BookInfoActivity extends AppCompatActivity {
    private Context ctx;
    public static final String VIEW_TYPE = "view_type";
    public static final String OWNER_VIEW = "owner_view_book_info";
    public static final String BORROWER_VIEW = "borrower_view_book_info";
    public static final String REQUSET_VIEW = "request_view_book_info";
    private String view_type;

    public static final String BOOK_ID = "bookID";

    String title;
    String author;
    long isbn;
    String desc;
    private String bookID;
    private String borrowerID;
    private String ownerID;
    private BookInfoViewModel bookInfoViewModel;

    private User loggedInUser;
    private DatabaseReference loggedInUserRef;
    private ValueEventListener loggedInUserFirebaseListener;

    private TextView textTitle;
    private TextView textAuthor;
    private TextView textIsbn;
    private TextView textDesc;
    private TextView textStatus;
    private ImageView bookImage;

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

    private Book book;

    private LinearLayout borrowerProfileArea;
    private LinearLayout ownerProfileArea;

    private static final String TAG = BookInfoActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseRecyclerAdapter.startListening();
        loggedInUserRef.addListenerForSingleValueEvent(loggedInUserFirebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            // null check because this isn't always initialized
            firebaseRecyclerAdapter.stopListening();
        }

        loggedInUserRef.removeEventListener(loggedInUserFirebaseListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.activity_book_info);
        ctx = this;

        Intent intent = getIntent();
        view_type = intent.getStringExtra(VIEW_TYPE);
        Log.i(TAG, "view type : " + view_type);
        bookID = intent.getStringExtra("bookID");
        Log.i(TAG, "bookid value: " + bookID);

        textTitle = (TextView) findViewById(R.id.bookTitle);
        textAuthor = (TextView) findViewById(R.id.bookAuthor);
        textIsbn = (TextView) findViewById(R.id.bookISBN);
        textDesc = (TextView) findViewById(R.id.bookDescription);
        textStatus = (TextView) findViewById(R.id.bookStatus);
        bookImage = (ImageView)findViewById(R.id.bookImage);

        borrowerProfileArea = (LinearLayout) findViewById(R.id.borrower_prof_area);
        ownerProfileArea = (LinearLayout) findViewById(R.id.owner_prof_area);

        FirstBookPhotoViewModelFactory bookPhotoViewModelFactory = new FirstBookPhotoViewModelFactory(bookID);
        FirstBookPhotoViewModel bookPhotoViewModel = ViewModelProviders
                                                        .of(this, bookPhotoViewModelFactory)
                                                        .get(FirstBookPhotoViewModel.class);
        bookPhotoViewModel.getBookPhotoLiveData().observe(this, new Observer<Photo>() {
            @Override
            public void onChanged(@Nullable Photo photo) {
                Bitmap bitmapPhoto = (photo != null)
                        ? Photo.DecodeBase64BitmapPhoto(photo.getEncodedString())
                        : null;
                // The fallback image will be displayed if bitmapPhoto is null
                Glide.with(getApplicationContext())
                        .load(bitmapPhoto)
                        .apply(new RequestOptions()
                                .fallback(R.drawable.ic_book_black_150dp)
                                .centerCrop()
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(bookImage);
            }
        });

        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(final @Nullable Book book) {
                if (book != null) {
                    BookInfoActivity.this.book = book;

                    textTitle.setText(book.getTitle());
                    textAuthor.setText(book.getAuthor());
                    textIsbn.setText(String.valueOf(book.getIsbn()));
                    textDesc.setText(book.getDescription());
                    textStatus.setText(String.valueOf(book.getStatus()));
                    setStatusTextColor(book);

                    setupViewComponents(book);
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

        hideGoodreadsReview();
        showGoodreadsReviewError("Loading...\n");
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    long isbn = book.getIsbn();
                    getGoodreadsReviewInfo(isbn);

                    getReviewsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoReviewsActivity(goodreadsReviewInfo.getReviews_widget_url());
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getIntent().getStringExtra(VIEW_TYPE).equals(OWNER_VIEW)){
            getMenuInflater().inflate(R.menu.menu_book_info, menu);
        }
        else if(getIntent().getStringExtra(VIEW_TYPE).equals(BORROWER_VIEW)) {
            // TODO
        }
        else if (getIntent().getStringExtra(VIEW_TYPE).equals(REQUSET_VIEW)) {

        }
        else {
            Log.e(TAG, "ERROR in view type onCreateOptionsMenu");
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.form_edit_book_photos:
                Log.i(TAG, "edit photos clicked!");
                if (book != null) {
                    Intent intent = new Intent(BookInfoActivity.this, ViewEditBookPhotosActivity.class);
                    intent.putExtra(ViewEditBookPhotosActivity.INTENT_BOOK_ID, bookID);
                    intent.putExtra(ViewEditBookPhotosActivity.INTENT_OWNER_USER_ID, book.getOwnerID());
                    startActivity(intent);
                }
                return true;

            case R.id.form_edit_book:
                Log.i(TAG, "edit book clicked!");
                editBook();
                return true;

            case R.id.form_delete_book:
                Log.i(TAG, "delete book clicked!");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete_book_dialog_prompt))
                        .setPositiveButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBook();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do Nothing
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        Log.i(TAG, "in editBook()");
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
        Log.i(TAG, "in deleteBook()");

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
                            if (error != null && error.getMessage() != null && !error.getMessage().equals("")) {
                                Log.e(TAG, error.getMessage());
                                Log.e(TAG, error.toString());
                            }
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
    }

    private void hideRequestStatus() {
        LinearLayout requestStatusArea = (LinearLayout) findViewById(R.id.request_status_area);
        requestStatusArea.setVisibility(View.GONE);
    }

    private void showRequestStatus(final Book book) {
        LinearLayout requestStatusArea = (LinearLayout) findViewById(R.id.request_status_area);
        // set the request status text
        final TextView requestStatusTextView = (TextView) findViewById(R.id.requestStatus);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userID != null && !userID.equals("")) {
            // get the request on this book
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference()
                    .child(getString(R.string.db_requestCollection))
                    .child(userID)
                    .child(book.getBookID())
                    .child("rStatus");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String requestStatus = dataSnapshot.getValue(String.class);
                    Log.i(TAG, "Request status: " + requestStatus);
                    // update the request status text
                    requestStatusTextView.setText((String) requestStatus);
                    // set color
                    if(requestStatus.equals("PENDING")){
                        requestStatusTextView.setTextColor(Color.BLUE);
                    }
                    else if(requestStatus.equals("ACCEPTED")){
                        requestStatusTextView.setTextColor(Color.GREEN);
                    }
                    else if(requestStatus.equals("DECLINED")){
                        requestStatusTextView.setTextColor(Color.RED);
                    }
                    else {
                        Log.e(TAG, "Bad request status, shouldn't be here");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(TAG, "cancelled reading for requests.");
                }
            });

            requestStatusArea.setVisibility(View.VISIBLE);
        }
        else {
            Log.e(TAG, "No logged in user, shouldn't be here");
        }


    }

    private void hideRequestBtn() {
        LinearLayout requestBtnArea = (LinearLayout) findViewById(R.id.requestBookBtnArea);
        requestBtnArea.setVisibility(View.GONE);
    }

    private void showRequestBtn(final Book book_to_request) {
        LinearLayout requestBtnArea = (LinearLayout) findViewById(R.id.requestBookBtnArea);
        Button requestBookBtn = (Button) findViewById(R.id.requestBookBtn);

        requestBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // taken from the old AvailableBookInfoActivity
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference()
                        .child(getString(R.string.db_users)).child(currentUser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User requester = dataSnapshot.getValue(User.class);

                            com.example.atheneum.models.Request newRequest = new com.example.atheneum.models.Request(requester.getUserID(), book_to_request.getBookID());

                            Notification notification = new Notification(
                                    requester.getUserID(),
                                    book_to_request.getOwnerID(),
                                    book_to_request.getOwnerID(),
                                    book_to_request.getBookID(),
                                    Notification.NotificationType.REQUEST,
                                    "");
                            notification.constructMessage(requester.getUserName(), book_to_request.getTitle());
                            DatabaseWriteHelper.makeRequest(newRequest, notification);

                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        requestBookBtn.setVisibility(View.VISIBLE);
    }

    private void hideOwnerProfArea() {
        ownerProfileArea = (LinearLayout) findViewById(R.id.owner_prof_area);
        ownerProfileArea.setVisibility(View.GONE);
    }

    private void showOwnerProfArea(final Book book) {
        ownerID = book.getOwnerID();

        // setup and show owner username and picture
        final TextView ownererEmailTextView = (TextView) findViewById(R.id.book_owner_email);
        final ImageView ownerPicture = (ImageView) findViewById(R.id.owner_profile_pic);
        // retrieve the User's email
        if (!(ownerID == null || ownerID.equals(""))) {
            Log.i(TAG, "Valid ownerID, this is expected");

            // retrieve email
            String ownerProviderKey = UserViewModel.generateViewModelProviderKey(ownerID);
            UserViewModel ownerViewModel = ViewModelProviders
                    .of(BookInfoActivity.this, new UserViewModelFactory(ownerID))
                    .get(ownerProviderKey, UserViewModel.class);
            final LiveData<User> ownerLiveData = ownerViewModel.getUserLiveData();

            ownerLiveData.observe(BookInfoActivity.this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    Log.i(TAG, "in Observer!");
                    // update borrower email
                    ownererEmailTextView.setText(user.getUserName());
                    Log.i(TAG, "User email*** " + user.getUserName());
                    if (user.getPhotos().size() > 0) {
                        String userPic = user.getPhotos().get(0);
                        Bitmap bitmapPhoto = PhotoUtils.DecodeBase64BitmapPhoto(userPic);
                        ownerPicture.setImageBitmap(bitmapPhoto);
                    } else {
                        ownerPicture.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));
                    }
                    ownerPicture.setVisibility(View.VISIBLE);
                    // show image
                    // Remove the observer after update
                    ownerLiveData.removeObserver(this);
                }
            });

            // set clickable profile area
            ownerProfileArea.setClickable(true);
            ownerProfileArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Owner Profile clicked");
                    viewUserProfile(ownerID);
                }
            });

            ownerProfileArea.setVisibility(View.VISIBLE);
        }
        else { // shouldn't be here
            Log.e(TAG, "Error, no ownerID found");
            ownerProfileArea.setVisibility(View.GONE);
        }
    }

    private void hideBorrowerProfArea() {
        borrowerProfileArea = (LinearLayout) findViewById(R.id.borrower_prof_area);
        borrowerProfileArea.setVisibility(View.GONE);
    }

    private void showBorrowerProfArea(final Book book) {
        borrowerProfileArea = (LinearLayout) findViewById(R.id.borrower_prof_area);
        borrowerID = book.getBorrowerID();

        // using borrowerID, show borrower email and profile image, or "None"
        final TextView borrowerEmailTextView = (TextView) findViewById(R.id.book_borrower_email);
        final ImageView borrowerPicture = (ImageView) findViewById(R.id.borrower_profile_pic);
        // retrieve the User's email
        if (!(borrowerID == null || borrowerID.equals(""))) {
            Log.i(TAG, "Valid borrowerID");

            // retrieve email
            String borrowerProviderKey = UserViewModel.generateViewModelProviderKey(borrowerID);
            UserViewModel borrowerViewModel = ViewModelProviders
                    .of(BookInfoActivity.this, new UserViewModelFactory(borrowerID))
                    .get(borrowerProviderKey, UserViewModel.class);
            final LiveData<User> borrowerLiveData = borrowerViewModel.getUserLiveData();

            borrowerLiveData.observe(BookInfoActivity.this, new Observer<User>() {
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
                    borrowerLiveData.removeObserver(this);
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
            borrowerProfileArea.setVisibility(View.VISIBLE);
        }
        else { // no borrower;
            Log.i(TAG, "No borrower");
            borrowerEmailTextView.setText("None");
            borrowerPicture.setVisibility(View.INVISIBLE);  // hide profile picture placeholder when no borrower
            borrowerProfileArea.setClickable(false);
        }
    }

    private void hideRequesterList(){
        LinearLayout requesterListArea = (LinearLayout) findViewById(R.id.requesterListArea);
        requesterListArea.setVisibility(View.GONE);
    }

    private void showRequesterList() {
        LinearLayout requesterListArea = (LinearLayout) findViewById(R.id.requesterListArea);

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

            firebaseRecyclerAdapter.startListening();

            requestsRecyclerView = (RecyclerView) findViewById(R.id.book_requests_recycler_view);
            requestsLayoutManager = new LinearLayoutManager(this);
            requestsRecyclerView.setLayoutManager(requestsLayoutManager);
            requestsRecyclerView.setAdapter(firebaseRecyclerAdapter);
            requestsRecyclerView.setNestedScrollingEnabled(false);
            requestsRecyclerView.addItemDecoration(new DividerItemDecoration(requestsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
        }

        requesterListArea.setVisibility(View.VISIBLE);
    }

    private void setupViewComponents(final Book book) {
        if (this.view_type == null || this.view_type.equals("")){
            Log.e(TAG, "No view type, shouldn't be happening");
            return;
        }

        if (view_type.equals(OWNER_VIEW)) {
            // owner view shouldn't show owner or request button
            hideRequestStatus();
            hideRequestBtn();
            hideOwnerProfArea();
            showBorrowerProfArea(book);
            showRequesterList();
        }
        else if (view_type.equals(BORROWER_VIEW)) {
            hideRequestStatus();
            showRequestBtn(book);
            showOwnerProfArea(book);
            hideBorrowerProfArea();
            hideRequesterList();
        }
        else if (view_type.equals(REQUSET_VIEW)) {
            showRequestStatus(book);
            hideRequestBtn();
            showOwnerProfArea(book);
            showBorrowerProfArea(book);
            hideRequesterList();
        }
        else {
            Log.e(TAG, "invalid view type, shouldn't be happening");
        }

    }
}
