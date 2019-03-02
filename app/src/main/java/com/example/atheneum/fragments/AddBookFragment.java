package com.example.atheneum.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBookFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;
    private Context context;

    private User owner;
    private Book newBook;
    String title;
    String author;
    long isbn;
    String desc;

    // references for the various entry fields
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private EditText descEditText;

    private FloatingActionButton saveBtn;
    private Button scanIsbnBtn;
    private Button autoPopulateByIsgnBtn;


    public AddBookFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_add_edit_book, container, false);

        this.context = getContext();

        // bind editText references to UI elements
        titleEditText = this.view.findViewById(R.id.bookTitleEditText);
        authorEditText = this.view.findViewById(R.id.authorEditText);
        isbnEditText = this.view.findViewById(R.id.isbnEditText);
        descEditText = this.view.findViewById(R.id.descEditText);

        saveBtn = this.view.findViewById(R.id.saveBookBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewBook();
            }
        });
        scanIsbnBtn = this.view.findViewById(R.id.scanIsbnButton);
        scanIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIsbn();
            }
        });
        autoPopulateByIsgnBtn = this.view.findViewById(R.id.populateFromIsbnBtn);
        autoPopulateByIsgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFieldsByIsbn();
            }
        });

        mainActivity = (MainActivity) getActivity();
        // set action bar title
        mainActivity.setActionBarTitle(context.getResources().getString(R.string.add_book_prompt));

        return this.view;
    }

    public void scanIsbn() {
        Log.i("AddBook", "AddBook*** ISBN scan requested");
        // TODO
    }

    public void populateFieldsByIsbn() {
        // TODO: FINISH

        Log.i("AddBook", "AddBook*** Auto-populate requested");
        this.isbn = -1;
        if (!isbnEditText.getText().toString().equals("")) {
             isbn = Integer.parseInt(isbnEditText.getText().toString());

        }
        else {
            Log.i("AddBook", "AddBook*** No ISBN ");
            // TODO: Print error message

        }

    }

    public boolean allFieldsFilled() {
        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        String isbn = isbnEditText.getText().toString();
        String desc = descEditText.getText().toString();

        boolean allFilled = !title.equals("") && !author.equals("") && !isbn.equals("")
                && !desc.equals("");

        return allFilled;
    }

    public void saveNewBook() {
        Log.i("AddBook", "AddBook*** Save Button Pressed");

        if (allFieldsFilled()) {
            Log.i("AddBook", "AddBook*** Fields checked, all filled");

            title = titleEditText.getText().toString();
            author = authorEditText.getText().toString();
            isbn = Long.parseLong(isbnEditText.getText().toString());
            desc = descEditText.getText().toString();

            // get user from Firebase auth
//            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final FirebaseDatabase db = FirebaseDatabase.getInstance();

            if (firebaseUser != null) {
                Log.i("AddBook", "AddBook*** not null firebaseuser");

                DatabaseReference ref = db.getReference().child(getString(R.string.db_users)).child(firebaseUser.getUid());
                // retrieve user object from database
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i("AddBook", "AddBook*** Reading for user");
                        if (dataSnapshot.exists()) {
                            owner = dataSnapshot.getValue(User.class);
                            Log.i("AddBook", "AddBook*** Got a user");

                            newBook = new Book(isbn, title, desc, author, owner, null, Book.Status.AVAILABLE);

                            // add book to the owner's collection
                            DatabaseReference ownerColref = db.getReference().child(getString(R.string.db_ownerCollection)).child(owner.getUserID());
                            ownerColref.child(newBook.getBookID().toString()).setValue(newBook);
                            Log.i("AddBook", "Book added, id=" + newBook.getBookID().toString());

                            // hide keyboard and close fragment
                            // keyboard hiding taken from:
                            // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
                            InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            mainActivity.getSupportFragmentManager().beginTransaction().remove(AddBookFragment.this).commit();

                            // return to home page fragment
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();


                        } else {
                            Log.w("AddBook", "AddBook*** Current User doesn't exist in database!");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("AddBook", "AddBook*** User listener was cancelled");
                    }
                });



            } else {
                Log.w("AddBook", "AddBook*** ERROR UNAUTH USER : User should be authenticated if the user is in this screen!");
            }



        }
        else {
            // TODO : Display Error Prompt
            Log.w("AddBook", "AddBook*** Error fields unfilled");
        }

    }

}
