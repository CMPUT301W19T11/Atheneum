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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Photo;
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
import com.example.atheneum.viewmodels.FirstBookPhotoViewModel;
import com.example.atheneum.viewmodels.FirstBookPhotoViewModelFactory;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Activity for adding and editing a book. Provides the UI fields and buttons for inputting
 * attribute values of a new book.
 */
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
    private ImageView bookImage;

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
        context = this.getApplicationContext();

        // bind editText references to UI elements
        titleEditText = findViewById(R.id.bookTitleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        isbnEditText = findViewById(R.id.isbnEditText);
        descEditText = findViewById(R.id.descEditText);

        bookImage = findViewById(R.id.bookImage);

        bookID = getIntent().getStringExtra("BookID");
        if(bookID != null && !bookID.equals("")) {
            // populate fields with existing book info if activity was entered for editing
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

            bookImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final LiveData<Book> bookLiveData = bookInfoViewModel.getBookLiveData();
                    bookLiveData.observe(AddEditBookActivity.this, new Observer<Book>() {
                        @Override
                        public void onChanged(@Nullable Book book) {
                            Intent intent = new Intent(AddEditBookActivity.this, ViewEditBookPhotos.class);
                            intent.putExtra(ViewEditBookPhotos.INTENT_BOOK_ID, bookID);
                            if (book != null) {
                                intent.putExtra(ViewEditBookPhotos.INTENT_OWNER_USER_ID, book.getOwnerID());
                            }
                            startActivity(intent);
                            bookLiveData.removeObserver(this);
                        }
                    });
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
                if(bookID != null && !bookID.equals("")) { // not null bookID implies existing book(edit)
                    updateBook();
                }
                else{ // create new book
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

    /**
     * Start activity for scanning the ISBN barcode
     *
     * @param v the current view
     */
    public void scanIsbn(View v) {
        Log.i(TAG, "AddBook*** ISBN scan requested");

        Intent intent = new Intent(this, ScanBarcodeActivity.class);

        startActivityForResult(intent, 0);
    }

    /**
     * Overridden activity result handler.
     * For now should only be used to check ScanBarcodeActivity results, in which case it attempts
     * to populate the ISBN field and call the API request to autofill the rest of the fields
     *
     * @param requestCode the request code of the started activity
     * @param resultCode result code of the finished activity
     * @param data intent data of the finished activity
     */
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

    /**
     * Populate all input fields using a given ISBN and a call to the Google Books API
     * Reads the ISBN from the input field. Will not populate if there is not internet connection,
     * or the ISBN is invalid or doesn't exist in Google Books.
     */
    public void populateFieldsByIsbn() {
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


            // Add the request to the RequestQueue. This is part of an app-wide singleton
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);


        } else {
            Log.i(TAG, "AddBook*** No ISBN, can't autofill ");
            Toast.makeText(this, "Cannot autofill without ISBN", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Checks whether all inputs have been filled
     * @return whether or not all required inputs were filled
     */
    public boolean allFieldsFilled() {
        boolean allFilled = !TextUtils.isEmpty(titleEditText.getText()) &&
                !TextUtils.isEmpty(authorEditText.getText()) &&
                !TextUtils.isEmpty(isbnEditText.getText()) &&
                !TextUtils.isEmpty(descEditText.getText());

        return allFilled;
    }

    /**
     * Update the firebase data of an existing book. Used when this activity is spawned for
     * editing.
     */
    public void updateBook(){
        Log.i(TAG, "UpdateBook*** Save Button Pressed");

        if (!allFieldsFilled()) {
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

    /**
     * Save a new book to firebase, attached to the owner that created it, using the provided
     * data in the input fields.
     * If the fields were not correctly filled, no book is saved and an error is shown.
     */
    public void saveNewBook() {
//        Log.i(TAG, "AddBook*** Save Button Pressed");
        // prevent additional clicks
        saveBtn.setClickable(false);

        Log.i(TAG, "save new book!");
        if (!allFieldsFilled()) {
            // TODO : Display Error Prompt
            Log.w(TAG, "AddBook*** Error fields unfilled");
            Toast.makeText(context, "Cannot save with unfilled fields!", Toast.LENGTH_SHORT).show();

            // call all input validations to show error prompts
            for (EditTextWithValidator editTextVal : editTextWithValidatorArray) {
                editTextVal.validator.validate(editTextVal.editText);
            }
            // re-enable clicks
            saveBtn.setClickable(true);
            return;
        }

        // clicks don't need to be re-enabled here because all fields have been verified to be filled
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
                }
            });

            finish(); // finish outside of handler to prevent multiple clicks
        }

    }
}
