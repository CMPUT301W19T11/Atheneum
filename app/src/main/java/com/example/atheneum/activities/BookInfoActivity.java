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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.OwnerBooksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookInfoActivity extends AppCompatActivity {

    private static final int BOOK_INFO_DELETED =0;
    private static final int BOOK_INFO_EDITED = 1;


    private User owner;
    private Book book;
    String title;
    String author;
    long isbn;
    String desc;
    private String bookID;

    private TextView textTitle;
    private TextView textAuthor;
    private TextView textIsbn;
    private TextView textDesc;
    private TextView textStatus;

    private Button deleteBtn;
    private Button editBtn;

    private static final String TAG = "viewBookInfo";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        deleteBtn = findViewById(R.id.buttonDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });
        editBtn = findViewById(R.id.buttonEdit);
        //TODO: implement edit book button

        retrieveBookInfo();

    }

    public void retrieveBookInfo(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("ownerCollection").child(firebaseUser.getUid()).
                child(bookID);
        if(firebaseUser != null){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Book book = dataSnapshot.getValue(Book.class);
                    textTitle.setText(book.getTitle());
                    textAuthor.setText(book.getAuthor());
                    textIsbn.setText(String.valueOf(book.getIsbn()));
                    textDesc.setText(book.getDescription());
                    textStatus.setText(String.valueOf(book.getStatus()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        }

    }


    public void deleteBook(){
        Log.i(TAG, "Delete book button pressed");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("ownerCollection"). child(firebaseUser.getUid());
        if(firebaseUser != null) {
            ref.child(bookID).removeValue();
        }
        //int position = Integer.valueOf(getIntent().getStringExtra("position"));
        //Intent intent = new Intent();
        //intent.putExtra(OwnerBooksAdapter.REQUEST_DELETE_ENTRY, BOOK_INFO_CHANGED);
        //setResult(OwnerBooksAdapter.REQUEST_DELETE_ENTRY);




    }

}