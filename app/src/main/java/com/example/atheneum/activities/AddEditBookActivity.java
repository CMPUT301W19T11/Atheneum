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
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.atheneum.R;
import com.example.atheneum.controllers.PictureController;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.SingletonRequestQueue;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.ConnectionChecker;
import com.example.atheneum.utils.EditTextWithValidator;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.NonEmptyTextValidator;
import com.example.atheneum.viewmodels.AddBookViewModel;
import com.example.atheneum.viewmodels.AddBookViewModelFactory;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.vision.barcode.Barcode;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddEditBookActivity extends AppCompatActivity {

    private Context context;
    private BookInfoViewModel bookInfoViewModel;

    String title;
    String author;
    long isbn;
    String desc;

    // references for the various entry fields
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private EditText descEditText;
    private EditTextWithValidator[] editTextWithValidatorArray;

    private FloatingActionButton saveBtn;
    private Button scanIsbnBtn;
    private Button autoPopulateByIsbnBtn;

    private String bookID;

    private static final String TAG = AddEditBookActivity.class.getSimpleName();
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.view = findViewById(android.R.id.content);

        // bind editText references to UI elements
        titleEditText = findViewById(R.id.bookTitleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        isbnEditText = findViewById(R.id.isbnEditText);
        descEditText = findViewById(R.id.descEditText);

        bookID = getIntent().getStringExtra("BookID");
        if( !bookID.equals("")){

            BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
            bookInfoViewModel = ViewModelProviders.of(this, factory).get(BookInfoViewModel.class);
            final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
            bookLiveData.observe(this, new Observer<Book>() {
                @Override
                public void onChanged(@Nullable Book book) {
                    if (book != null) {
                        titleEditText.setText(book.getTitle());
                        authorEditText.setText(book.getAuthor());
                        isbnEditText.setText(String.valueOf(book.getIsbn()));
                        descEditText.setText(book.getDescription());
                    }
                    bookLiveData.removeObserver(this);
                }
            });
        }

        // Setup all edit texts with their validators
        editTextWithValidatorArray = new EditTextWithValidator[]{
                new EditTextWithValidator(titleEditText, new NonEmptyTextValidator(titleEditText)),
                new EditTextWithValidator(authorEditText, new NonEmptyTextValidator(authorEditText)),
                new EditTextWithValidator(isbnEditText, new NonEmptyTextValidator(isbnEditText)),
                new EditTextWithValidator(descEditText, new NonEmptyTextValidator(descEditText)),
        };



        saveBtn = findViewById(R.id.saveBookBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bookID.equals("")) {
                    updateBook();
                }
                else{
                    saveNewBook();
                }
            }
        });
        scanIsbnBtn = findViewById(R.id.scanIsbnButton);
        scanIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIsbn(v);
            }
        });
        autoPopulateByIsbnBtn = findViewById(R.id.populateFromIsbnBtn);
        autoPopulateByIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFieldsByIsbn();
            }
        });
    }

    public void scanIsbn(View v) {
        Log.i(TAG, "AddBook*** ISBN scan requested");

        Intent intent = new Intent(this, ScanBarcodeActivity.class);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(TAG, "Return from scan ISBN");
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("Barcode");
                    isbnEditText.setText(String.valueOf(barcode.displayValue));
                    populateFieldsByIsbn();
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

    public void populateFieldsByIsbn() {
        // TODO: FINISH

        // Check internet connection
        if (!(new ConnectionChecker(this)).isNetworkConnected()){ // no internet connection
            Toast.makeText(this, "Error, no Internet connection", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.i(TAG, "AddBook*** Auto-populate requested");
        this.isbn = Book.INVALILD_ISBN;
        if (!TextUtils.isEmpty(isbnEditText.getText())) {
            String isbn_str = isbnEditText.getText().toString();
            // auto populate with Google Books API
            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn_str;

            // need final instances of these EditTexts to be usable inside the lambda below
            final EditText titleEditText = this.view.findViewById(R.id.bookTitleEditText);
            final EditText authorEditText = this.view.findViewById(R.id.authorEditText);
            final EditText descEditText = this.view.findViewById(R.id.descEditText);

            final Context ctx = this;

            // taken from https://developer.android.com/training/volley/request.html
            // Request a JSON response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, apiUrlString, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // check if at least one item exists with that isbn
                            try {
                                if (response.getInt("totalItems") > 0) {
                                    JSONObject firstBookInfo = response.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

                                    titleEditText.setText(firstBookInfo.getString("title"));
                                    JSONArray authorArr = firstBookInfo.getJSONArray("authors");
                                    String authorListString = authorArr.length() > 0 ? authorArr.getString(0) : "";
//
                                    for (int i = 1; i < authorArr.length(); i++) {
                                        authorListString += ", " + authorArr.getString(i);
                                    }
                                    authorEditText.setText(authorListString);

                                    descEditText.setText(firstBookInfo.getString("description"));
                                }
                                else{
                                    Toast.makeText(ctx, "No books found for given ISBN", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch(Exception e) {
                                Log.e(TAG, "AddBook *** JSONObject error");
                                Toast.makeText(ctx, "Error - Couldn't populate fields", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.e(TAG, "AddBook *** VolleyError");
                            Toast.makeText(ctx, "Error - Volley couldn't access API", Toast.LENGTH_SHORT).show();

                        }
                    });


            // Add the request to the RequestQueue.
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);


        } else {
            Log.i(TAG, "AddBook*** No ISBN, can't autofill ");
            Toast.makeText(this, "Cannot autofill without ISBN", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean allFieldsFilled() {
        boolean allFilled = !TextUtils.isEmpty(titleEditText.getText()) &&
                !TextUtils.isEmpty(authorEditText.getText()) &&
                !TextUtils.isEmpty(isbnEditText.getText()) &&
                !TextUtils.isEmpty(descEditText.getText());

        return allFilled;
    }

    public void updateBook(){
        Log.i(TAG, "UpdateBook*** Save Button Pressed");

        if (!allFieldsFilled()) {
            // TODO : Display Error Prompt
            Log.w(TAG, "AddBook*** Error fields unfilled");
            Toast.makeText(context, "Cannot save with unfilled fields!", Toast.LENGTH_SHORT).show();

            // call all input validations to show error prompts
            for (EditTextWithValidator editTextVal : editTextWithValidatorArray) {
                editTextVal.validator.validate(editTextVal.editText);
            }
            return;
        }
        Log.i(TAG, "UpdateBook*** Fields checked, all filled");

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            BookInfoViewModelFactory factory = new BookInfoViewModelFactory(bookID);
            bookInfoViewModel = ViewModelProviders.of(AddEditBookActivity.this, factory).get(BookInfoViewModel.class);
            final LiveData<Book>  bookLiveData = bookInfoViewModel.getBookLiveData();
            bookLiveData.observe(this, new Observer<Book>() {
                @Override
                public void onChanged(@Nullable Book book) {
                    if (book != null) {
                        Log.i(TAG, "updateBook(): got book" + book.toString());
                        book.setTitle(titleEditText.getText().toString());
                        book.setAuthor(authorEditText.getText().toString());
                        book.setIsbn(Long.parseLong(isbnEditText.getText().toString()));
                        book.setDescription(descEditText.getText().toString());
                        bookInfoViewModel.updateBook(book);
                    }
                    bookLiveData.removeObserver(this);
                    finish();
                }
            });
        }
    }

    public void saveNewBook() {
//        Log.i(TAG, "AddBook*** Save Button Pressed");

        Log.i(TAG, "save new book!");
        if (!allFieldsFilled()) {
            // TODO : Display Error Prompt
            Log.w(TAG, "AddBook*** Error fields unfilled");
            Toast.makeText(context, "Cannot save with unfilled fields!", Toast.LENGTH_SHORT).show();

            // call all input validations to show error prompts
            for (EditTextWithValidator editTextVal : editTextWithValidatorArray) {
                editTextVal.validator.validate(editTextVal.editText);
            }
            return;
        }

        Log.i(TAG, "AddBook*** Fields checked, all filled");

        title = titleEditText.getText().toString();
        author = authorEditText.getText().toString();
        isbn = Long.parseLong(isbnEditText.getText().toString());
        desc = descEditText.getText().toString();

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            Log.i(TAG, "owner authenticated: " + firebaseUser.getUid());
            AddBookViewModelFactory factory = new AddBookViewModelFactory(firebaseUser.getUid());
            final AddBookViewModel addBookViewModel = ViewModelProviders.of(AddEditBookActivity.this, factory).get(AddBookViewModel.class);
            final LiveData<User> userLiveData = addBookViewModel.getOwnerLiveData();
            userLiveData.observe(AddEditBookActivity.this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User owner) {
                    Log.i(TAG, "In saveNewBook onChanged()!");
                    if (owner != null) {
                        Log.i(TAG, "Got owner "+ owner.toString());
                        Book newBook = new Book(isbn, title, desc, author, owner, null, Book.Status.AVAILABLE);
                        Log.i(TAG, "newBook " + newBook.toString());
                        addBookViewModel.addBook(owner, newBook);
                    } else {
                        Log.i(TAG, "owner is null!");
                    }
                    userLiveData.removeObserver(this);
                    finish();
                }
            });
        }

    }
}