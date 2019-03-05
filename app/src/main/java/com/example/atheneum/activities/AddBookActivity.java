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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.fragments.AddBookFragment;
import com.example.atheneum.fragments.OwnerPageFragment;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.EditTextWithValidator;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.NonEmptyTextValidator;
import com.example.atheneum.viewmodels.AddBookViewModel;
import com.example.atheneum.viewmodels.AddBookViewModelFactory;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBookActivity extends AppCompatActivity {

    private Context context;
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
    private Button autoPopulateByIsgnBtn;

    private static final String TAG = "AddBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        // bind editText references to UI elements
        titleEditText = findViewById(R.id.bookTitleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        isbnEditText = findViewById(R.id.isbnEditText);
        descEditText = findViewById(R.id.descEditText);

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
                saveNewBook();
            }
        });
        scanIsbnBtn = findViewById(R.id.scanIsbnButton);
        scanIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIsbn();
            }
        });
        autoPopulateByIsgnBtn = findViewById(R.id.populateFromIsbnBtn);
        autoPopulateByIsgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFieldsByIsbn();
            }
        });
    }

    public void scanIsbn() {
        Log.i(TAG, "AddBook*** ISBN scan requested");
        // TODO
    }

    public void populateFieldsByIsbn() {
        // TODO: FINISH

        Log.i(TAG, "AddBook*** Auto-populate requested");
        this.isbn = Book.INVALILD_ISBN;
        if (!TextUtils.isEmpty(isbnEditText.getText())) {
            isbn = Integer.parseInt(isbnEditText.getText().toString());

        } else {
            Log.i(TAG, "AddBook*** No ISBN ");
            // TODO: Print error message

        }

    }

    public boolean allFieldsFilled() {
        boolean allFilled = !TextUtils.isEmpty(titleEditText.getText()) &&
                !TextUtils.isEmpty(authorEditText.getText()) &&
                !TextUtils.isEmpty(isbnEditText.getText()) &&
                !TextUtils.isEmpty(descEditText.getText());

        return allFilled;
    }

    public void saveNewBook() {
        Log.i(TAG, "AddBook*** Save Button Pressed");

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
            AddBookViewModelFactory factory = new AddBookViewModelFactory(firebaseUser.getUid());
            final AddBookViewModel addBookViewModel = ViewModelProviders.of(AddBookActivity.this, factory).get(AddBookViewModel.class);
            LiveData<User> userLiveData = addBookViewModel.getOwnerLiveData();
            userLiveData.observe(AddBookActivity.this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User owner) {
                    if (owner != null) {
                        Log.i(TAG, "Got owner "+ owner.toString());
                        Book newBook = new Book(isbn, title, desc, author, owner, null, Book.Status.AVAILABLE);
                        Log.i(TAG, "newBook " + newBook.toString());
                        addBookViewModel.addBook(owner, newBook);
                    } else {
                        Log.i(TAG, "owner is null!");
                    }
                    finish();
                }
            });
        }

    }
}
