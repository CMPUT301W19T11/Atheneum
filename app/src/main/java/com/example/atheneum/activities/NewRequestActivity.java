package com.example.atheneum.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Notification;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.requestAdapter;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class NewRequestActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    Intent intentNewRequest;
    Intent intentRequestList;

    private User requester;
    private EditText bookIDTest;
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private Button savebtn;
    private static final String TAG = "AddRequest";
    private static final String TAG1 = "Search Query";

    FirebaseUser currentUser;
    FirebaseDatabase db;
    DatabaseReference ref;
    ListView availableBookList;
    String currentUserID;


    private static ArrayList<Book> availableBook = new ArrayList<Book>();
    private static ArrayList<Book> defaultAvailableBook = new ArrayList<Book>();
    private static ArrayList<Book> searchAvailableBook = new ArrayList<Book>();
    private requestAdapter availableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);


        intentNewRequest = getIntent();
        intentRequestList = new Intent(this, MainActivity.class);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = currentUser.getUid();

        retriveBook();

        availableBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book book = (Book) availableBookList.getItemAtPosition(position);

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseDatabase.getInstance();
                ref = db.getReference()
                        .child(getString(R.string.db_users)).child(currentUser.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            requester = dataSnapshot.getValue(User.class);

                            Request newRequest = new Request(requester.getUserID(), book.getBookID());
                            // add book to the owner's collection
                            Log.i(TAG, "send added, id=" + newRequest.getBookID());

                            Notification notification = new Notification(
                                    requester.getUserID(),
                                    book.getOwnerID(),
                                    book.getOwnerID(),
                                    book.getBookID(),
                                    Notification.NotificationType.REQUEST,
                                    "");
                            DatabaseWriteHelper.makeRequest(newRequest, notification);


                            Log.i(TAG, "Request added, id=" + newRequest.getBookID());
                            startActivity(intentRequestList);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });


    }



    //See: https://stackoverflow.com/questions/34603157/how-to-get-a-text-from-searchview
    //See: https://developer.android.com/reference/android/widget/SearchView
    //See: https://www.youtube.com/watch?v=_7B5iuyhIFk
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        inflater.inflate(R.menu.search_menu, menu);
//        MenuItem item = menu.findItem(R.id.search_menu);

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_menu);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("books");

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "menu item collapse");
                availableAdapter = new requestAdapter(NewRequestActivity.this, R.layout.request_list_item, defaultAvailableBook);
                availableBookList.setAdapter(availableAdapter);
                return true;
            }
        });

        SearchView sv = new SearchView((getSupportActionBar().getThemedContext()));
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Log.d(TAG, "On Query Text Submit was Called");
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("books");


                ref.orderByChild("books").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        searchAvailableBook.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Book book = child.getValue(Book.class);
                            if(searchCheck(book, query)){
                                if(!searchAvailableBook.contains(book)){
                                    searchAvailableBook.add(book);
                                }
                            }

                        }
                        if (searchAvailableBook.isEmpty()) {
                            Toast.makeText(NewRequestActivity.this, "No exact matches found for search query", Toast.LENGTH_SHORT).show();
                        }
                        availableAdapter = new requestAdapter(NewRequestActivity.this, R.layout.request_list_item, searchAvailableBook);
                        availableBookList.setAdapter(availableAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG1, "On Cancelled of Options Menu");
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        sv.setIconifiedByDefault(false);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG1, "On CLICKC was Called");
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "OUTER ONQueryTextSubmit Called");
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange"+newText);
        return false;
    }


    /**
     * getting requested books done by current user
     */

    public void retriveBook(){
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("books");
        availableBookList = (ListView) this.findViewById(R.id.AvailableBookList);

        availableBook = new ArrayList<Book>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                availableBook.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    try {
                        Book book = child.getValue(Book.class);
                        if(book.getStatus().equals(Book.Status.AVAILABLE) || book.getStatus().equals(Book.Status.REQUESTED)) {
                            if(!book.getOwnerID().equals(currentUserID)){
                                if(!availableBook.contains(book)) {
                                    availableBook.add(book);
                                }
                            }
                        }
                    } catch (DatabaseException e) {
                        // TODO: Figure out why this exception even happens in the first place
                        Log.w(TAG, "child ref: " + child.getRef().toString());
                        Log.w(TAG, e.toString());
                    }
                    defaultAvailableBook = availableBook;

                }

                availableAdapter = new requestAdapter(NewRequestActivity.this, R.layout.request_list_item, availableBook);
                availableBookList.setAdapter(availableAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    /**
     * search an availabel book by a set of key words in description
     * @param book
     * @param query
     * @return
     */
    public boolean searchCheck(Book book, String query){
        Log.d(TAG, "Get Query "+query+query.length());

        ArrayList<String> queryList = new ArrayList<String>(Arrays.asList(query.toLowerCase().split(" ")));

        ArrayList<String> descriptionList = new ArrayList<String>(Arrays.asList(book.getDescription().toLowerCase().split(" ")));


        for (int i = 0; i<queryList.size(); i++){
            Log.d(TAG, "QUERY LIST IS "+ queryList);
            if(!descriptionList.contains(queryList.get(i))){
                return false;
            }
            else if(descriptionList.contains(queryList.get(i))){
                if(i==queryList.size()-1){
                    return true;
                }
            }

        }

        return false;
    }


}