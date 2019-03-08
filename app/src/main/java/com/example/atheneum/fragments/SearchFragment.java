package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.Toast;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * The fragment for Searching.
 * See: https://stackoverflow.com/questions/7230893/android-search-with-fragments
 * See: https://stackoverflow.com/questions/9343241/passing-data-between-a-fragment-and-its-container-activity
 * See: https://www.youtube.com/watch?v=jJYSm_yrT7I
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    /**
     * The User list view.
     */
    ListView userListView;
    /**
     * The User list.
     */
    ArrayList<User> userList;
    /**
     * The default user list with all user names
     */
    ArrayList<String> defaultUserNameList = new ArrayList<>();
    /**
     * The Database object for Firebase
     */
    FirebaseDatabase db;
    /**
     * The reference to the Firebase Database object.
     */
    DatabaseReference dbRef;

    private static final String TAG = "Search";

    //See: https://stackoverflow.com/questions/27425547/cannot-resolve-method-getsupportfragmentmanager-inside-fragment
    //See: https://stackoverflow.com/questions/7645880/listview-with-onitemclicklistener-android
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_search, container, false);

        this.context = getContext();

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.search_page_title));
        }

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("users");

        userListView = (ListView) this.view.findViewById(R.id.userListView);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //See: https://stackoverflow.com/questions/7073577/how-to-get-object-from-listview-in-setonitemclicklistener-in-android
            //See: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
            //See: https://stackoverflow.com/questions/12659747/call-an-activity-method-from-a-fragment
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                User selectedUser = (User) parent.getAdapter().getItem(position);
                User selectedUser = userList.get(position);
                ((MainActivity)getActivity()).passDataToViewProfileActivity(selectedUser);
            }
        });

        userList = new ArrayList<>();
        final ArrayList<String> userNameList = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "On Data Change was Called");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User aUser = child.getValue(User.class);
                    userList.add(aUser);
                    userNameList.add(aUser.getUserName());
                }
                defaultUserNameList = userNameList;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNameList);
                userListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
        dbRef = db.getReference("users");

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "menu item collapse");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, defaultUserNameList);
                userListView.setAdapter(adapter);
                return true;
            }
        });

        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "On Query Text Submit was Called");
                db = FirebaseDatabase.getInstance();
                dbRef = db.getReference("users");
                dbRef.orderByChild("userName").equalTo(query).addListenerForSingleValueEvent(new ValueEventListener() {
                    final ArrayList<String> userNameList = new ArrayList<>();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            User aUser = child.getValue(User.class);
                            userList.add(aUser);
                            userNameList.add(aUser.getUserName());
                        }
                        if (userNameList.isEmpty()) {
                            Toast.makeText(getActivity(), "No exact matches found for search query", Toast.LENGTH_SHORT).show();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNameList);
                        userListView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "On Cancelled of Options Menu");
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
//                Log.d(TAG, "On CLICKC was Called");
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
        return false;
    }
}

