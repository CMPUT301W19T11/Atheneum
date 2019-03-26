package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;


/**
 * Show details of requests made by current user
 */
public class ShowRequestInfoActivity extends AppCompatActivity {
    public static final String BOOK_ID = "bookID";
    public static final String RSTATUS = "rStatus";

    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookISBN;
    private TextView bookStatus;
    private TextView bookOwner;
    private TextView bookDescription;
    private TextView RequestStatus;
    private Button ownerDetails;

    private String bookID;
    private String rStaus;
    private String ownerID;

    private static final String TAG = "ShowRequest";

    private BookInfoViewModel bookInfoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bookTitle = (TextView) findViewById(R.id.bookTitle);
        bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        bookISBN = (TextView) findViewById(R.id.bookISBN);
        bookStatus = (TextView) findViewById(R.id.bookStatus);
        bookOwner = (TextView) findViewById(R.id.bookOwner);
        bookDescription = (TextView) findViewById(R.id.bookDescription);
        RequestStatus = (TextView) findViewById(R.id.RequestStatus);
        ownerDetails = (Button) findViewById(R.id.ownerDetails);

        Intent Message = getIntent();


        bookID = Message.getStringExtra("bookID");
        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    bookTitle.setText(book.getTitle());
                    bookAuthor.setText(book.getAuthor());
                    bookISBN.setText(String.valueOf(book.getIsbn()));
                    bookDescription.setText(book.getDescription());
                    bookStatus.setText(String.valueOf(book.getStatus()));

                    // retrieve the Owner's email
                    if (!(book.getOwnerID() == null || book.getOwnerID().equals(""))) {
                        Log.i(TAG, "Valid ownerID");
                        ownerID = book.getOwnerID();
                        // retrieve email
                        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(book.getOwnerID());
                        UserViewModel userViewModel = ViewModelProviders.of(ShowRequestInfoActivity.this, userViewModelFactory).get(UserViewModel.class);
                        final LiveData<User> userLiveData = userViewModel.getUserLiveData();

                        userLiveData.observe(ShowRequestInfoActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(@Nullable User user) {
                                Log.i(TAG, "in Observer!");
                                // update borrower email
                                bookOwner.setText(user.getUserName());
                                Log.i(TAG, "User email*** " + user.getUserName());
                                userLiveData.removeObserver(this);
                            }
                        });



                    }
                    else { // no borrower;
                        Log.i(TAG, "No owner");
                        bookOwner.setText("None");
                    }
                }
            }
        });

        rStaus = Message.getStringExtra("rStatus");
        RequestStatus.setText(rStaus);


        ownerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_profile_intent  = new Intent(ShowRequestInfoActivity.this, ViewProfileActivity.class);
                view_profile_intent.putExtra(ViewProfileActivity.USER_ID, ownerID);
                startActivity(view_profile_intent);
            }
        });

    }
}
