package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Transaction;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.TransactionViewModel;
import com.example.atheneum.viewmodels.TransactionViewModelFactory;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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

    private Button scanBook;

    private String bookID;
    private String rStaus;
    private String ownerID;

    private Book book;

    private static final String TAG = "ShowRequestInfoActivity";

    private BookInfoViewModel bookInfoViewModel;
    private User loggedInUser;


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
        scanBook = (Button) findViewById(R.id.scanBook);

        Intent Message = getIntent();


        bookID = Message.getStringExtra("bookID");
        BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
        bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
        final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
        bookLiveData.observe(this, new Observer<Book>() {
            @Override
            public void onChanged(@Nullable Book book) {
                if (book != null) {
                    ShowRequestInfoActivity.this.book = book;
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

        if (!rStaus.equals("ACCEPTED")){
            Log.i(TAG, "scan button not visible");
            Log.i(TAG, rStaus);
            scanBook.setVisibility(View.INVISIBLE);
        }
        else{
            Log.i(TAG, "scan button visible");
            scanBook.setVisibility(View.VISIBLE);
            scanBook.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "ISBN scan requested");
                    Intent intent = new Intent(ShowRequestInfoActivity.this, ScanBarcodeActivity.class);

                    startActivityForResult(intent, 0);
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG, "Return from scan ISBN");
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("Barcode");
                    String barcodeISBN = String.valueOf(barcode.displayValue);
                    if(!barcodeISBN.equals(bookISBN.getText().toString())){
                        Toast.makeText(this, "Error: The ISBN does not match that of the requested book",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i(TAG, "Updating transaction bScan");
                        TransactionViewModelFactory factory = new TransactionViewModelFactory(book.getBookID());
                        Log.i(TAG, "bOok  bookID is " + book.getBookID());
                        final TransactionViewModel transactionViewModel = ViewModelProviders.of(this, factory).get(TransactionViewModel.class);
                        final LiveData<Transaction> transactionLiveData = transactionViewModel.getTransactionLiveData();

                        if (!transactionLiveData.hasObservers()) {
                            transactionLiveData.observe(this,  new Observer<Transaction>() {
                                @Override
                                public void onChanged(@Nullable Transaction transaction) {
                                    if (transaction != null && !transaction.getOScan() && !transaction.getBScan()) {
                                        Log.i(TAG, "updateTransaction(): got transaction" + transaction.toString());
                                        transaction.setBScan(true);
                                        transaction.setOwnerID(ownerID);
                                        FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();
                                        Log.i(TAG, "UserID is: " +   currentUser.getUid());
                                        transaction.setBorrowerID(currentUser.getUid());
                                        Log.i(TAG, "value of BScan is" + String.valueOf(transaction.getBScan()));
                                        Log.i(TAG, "value of OScan is" + String.valueOf(transaction.getOScan()));
                                        transactionViewModel.updateTransaction(transaction);

                                    }
                                    transactionLiveData.removeObserver(this);
                                }
                            });
                        }

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

    //TODO
//    @Override
//    public void onBackPressed() {
//        Log.i(TAG, "back button pressed");
//        if(String.valueOf(bOok.getStatus()).equals("BORROWED")){
//            Log.i(TAG, "deleting the request");
//            DatabaseWriteHelper.deleteRequest();
//        }
//        finish();
//    }
}
