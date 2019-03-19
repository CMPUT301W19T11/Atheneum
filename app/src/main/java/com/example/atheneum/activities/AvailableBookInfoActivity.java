package com.example.atheneum.activities;

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
import com.example.atheneum.models.Notification;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.BookRequestViewHolder;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.RequestCollectionRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.UsersRefUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Detailed information of the selected available book
 * And new request is generated in this activity
 */
public class AvailableBookInfoActivity extends AppCompatActivity {
    String title;
    String author;
    long isbn;
    String desc;
    private String bookID;
    private String borrowerID;
    private BookInfoViewModel bookInfoViewModel;

    private User loggedInUser;
    private DatabaseReference loggedInUserRef;
    private ValueEventListener loggedInUserFirebaseListener;

    private TextView textTitle;
    private TextView textAuthor;
    private TextView textIsbn;
    private TextView textDesc;
    private TextView textStatus;
    private TextView textOwner;

    private RecyclerView requestsRecyclerView;
    private RecyclerView.LayoutManager requestsLayoutManager;

    private Button requestBtn;


    private LinearLayout borrowerProfileArea;

    private static final String TAG = "Make a new request";

    Book shownBook;

    FirebaseUser currentUser;
    FirebaseDatabase db;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_book_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent Message = getIntent();
        bookID = Message.getStringExtra("availableBookID");
        Log.d(TAG, "Request added, id= "+bookID);
        textTitle = (TextView) findViewById(R.id.bookTitle);
        textAuthor = (TextView) findViewById(R.id.bookAuthor);
        textIsbn = (TextView) findViewById(R.id.bookISBN);
        textDesc = (TextView) findViewById(R.id.bookDescription);
        textStatus = (TextView) findViewById(R.id.bookStatus);
        textOwner = (TextView) findViewById(R.id.bookOwner);
        requestBtn = (Button) findViewById(R.id.requestBook);


        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    shownBook = book;
                    textTitle.setText(book.getTitle());
                    textAuthor.setText(book.getAuthor());
                    textIsbn.setText(String.valueOf(book.getIsbn()));
                    textDesc.setText(book.getDescription());
                    textStatus.setText(String.valueOf(book.getStatus()));

                    // retrieve the Owner's email
                    if (!(book.getOwnerID() == null || book.getOwnerID().equals(""))) {
                        Log.i(TAG, "Valid borrowerID");

                        // retrieve email
                        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(book.getOwnerID());
                        UserViewModel userViewModel = ViewModelProviders.of(AvailableBookInfoActivity.this, userViewModelFactory).get(UserViewModel.class);
                        final LiveData<User> userLiveData = userViewModel.getUserLiveData();

                        userLiveData.observe(AvailableBookInfoActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(@Nullable User user) {
                                Log.i(TAG, "in Observer!");
                                // update borrower email
                                textOwner.setText(user.getUserName());
                                Log.i(TAG, "User email*** " + user.getUserName());
                                userLiveData.removeObserver(this);
                            }
                        });



                    }
                    else { // no borrower;
                        Log.i(TAG, "No owner");
                        textOwner.setText("None");
                    }
                }
            }
        });

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent new_request_intent = new Intent(AvailableBookInfoActivity.this, MainActivity.class);

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseDatabase.getInstance();
                ref = db.getReference()
                        .child(getString(R.string.db_users)).child(currentUser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User requester = dataSnapshot.getValue(User.class);

                            Request newRequest = new Request(requester.getUserID(), shownBook.getBookID());
                            // add book to the owner's collection
//                            Log.i(TAG, "send added, id=" + newRequest.getBookID());
//
                            Notification notification = new Notification(
                                    requester.getUserID(),
                                    shownBook.getOwnerID(),
                                    shownBook.getOwnerID(),
                                    shownBook.getBookID(),
                                    Notification.NotificationType.REQUEST,
                                    "");
                            notification.constructMessage(requester.getUserName(), shownBook.getTitle());
                            DatabaseWriteHelper.makeRequest(newRequest, notification);

                            startActivity(new_request_intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });

    }

}
