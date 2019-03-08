package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.requestAdapter;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class newRequest extends Fragment implements SearchView.OnQueryTextListener{

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

    private static ArrayList<Book> availableBook = new ArrayList<Book>();
    private static ArrayList<Book> defaultAvailableBook = new ArrayList<Book>();
    private static ArrayList<Book> searchAvailableBook = new ArrayList<Book>();
    private requestAdapter availableAdapter;
    private String searchToken = "Search book by author/title/description";
//    private String searchToken = " ";

    /**
     * required empty constructor
     */
    public newRequest() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.new_request, container, false);

        this.context = getContext();

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));

        }

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("books");
        availableBookList = (ListView) this.view.findViewById(R.id.AvailableBookList);

        availableBook = new ArrayList<Book>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "On Data Change was Called");
                availableBook.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    if(book.getStatus() == Book.Status.AVAILABLE || book.getStatus() == Book.Status.REQUESTED) {
                        availableBook.add(book);
                    }
                    defaultAvailableBook = availableBook;

                }

                availableAdapter = new requestAdapter(newRequest.this.context, R.layout.request_list_item, availableBook);
                availableBookList.setAdapter(availableAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

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

//                            DatabaseReference requestColref = db.getReference()
//                                    .child(getString(R.string.db_requestCollection)).child(newRequest.getRequesterID());
//                            requestColref.child(newRequest.getBookID()).setValue(newRequest);

                            DatabaseWriteHelper.makeRequest(newRequest);

                            Log.i(TAG, "Request added, id=" + newRequest.getBookID());

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .remove(newRequest.this).commit();

                            // return to owner page fragment
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, new BorrowerPageFragment()).commit();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
        return this.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    //See: https://stackoverflow.com/questions/34603157/how-to-get-a-text-from-searchview
    //See: https://developer.android.com/reference/android/widget/SearchView
    //See: https://www.youtube.com/watch?v=_7B5iuyhIFk
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
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
                availableAdapter = new requestAdapter(newRequest.this.context, R.layout.request_list_item, defaultAvailableBook);
                availableBookList.setAdapter(availableAdapter);
                return true;
            }
        });

        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        //
        sv.setQuery(searchToken, false);
        sv.clearFocus();
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
                                searchAvailableBook.add(book);
                            }

                        }
                        if (searchAvailableBook.isEmpty()) {
                            Toast.makeText(getActivity(), "No exact matches found for search query", Toast.LENGTH_SHORT).show();
                        }
                        availableAdapter = new requestAdapter(newRequest.this.context, R.layout.request_list_item, searchAvailableBook);
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

        super.onCreateOptionsMenu(menu,inflater);
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
     * search an availabel book by title/author/description
     * @param book
     * @param query
     * @return
     */
    public boolean searchCheck(Book book, String query){
        Log.d(TAG1, "Get Query "+query+query.length());
        query = query.toLowerCase();
        if(query.length() == 0){return true;}
        Pattern r = Pattern.compile(query);
        Matcher m = r.matcher(book.getDescription().toLowerCase());
        if(book.getTitle().toLowerCase().equals(query)){ return true;}
        else if(book.getAuthor().toLowerCase().equals(query)){return true;}
        else if(m.find()){ return  true;}
        return false;
    }

}
