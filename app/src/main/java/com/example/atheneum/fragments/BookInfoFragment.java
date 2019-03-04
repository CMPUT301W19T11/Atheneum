/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URISyntaxException;
import java.util.UUID;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

public class BookInfoFragment extends Fragment {

    private View view;
    private MainActivity mainActivity;
    private Context context;

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

    private Button deleteBtn;
    private Button editBtn;

    private static final String TAG = "viewBookInfo";

    public BookInfoFragment(){
        //required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        bookID = getActivity().getIntent().getStringExtra("bookID");


        this.view = inflater.inflate(R.layout.book_info, container, false);

        this.context = getContext();

        textTitle = this.view.findViewById(R.id.bookTitle);
        textAuthor = this.view.findViewById(R.id.bookAuthor);
        textIsbn = this.view.findViewById(R.id.bookISBN);
        textDesc = this.view.findViewById(R.id.bookDescription);

        deleteBtn = this.view.findViewById(R.id.buttonDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });
        editBtn = this.view.findViewById(R.id.buttonEdit);
        //TODO: implement edit book button


        return this.view;
    }

    public void deleteBook(){
        Log.i(TAG, "Delete book button pressed");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("ownerCollection"). child(firebaseUser.getUid());
        ref.child(bookID).removeValue();
    }


}
