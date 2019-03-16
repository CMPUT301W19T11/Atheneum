package com.example.atheneum.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.SearchUsersViewModel;
import com.example.atheneum.views.adapters.UserListAdapter;
import java.util.List;

/**
 * The fragment for Searching.
 * See: https://stackoverflow.com/questions/7230893/android-search-with-fragments
 * See: https://stackoverflow.com/questions/9343241/passing-data-between-a-fragment-and-its-container-activity
 * See: https://www.youtube.com/watch?v=jJYSm_yrT7I
 */
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private View view;
    private MainActivity mainActivity = null;
    private SearchUsersViewModel searchUsersViewModel;

    private RecyclerView userListRecyclerView;
    private UserListAdapter userListAdapter;

    /**
     * Override onCreateView method of fragment to load search layout
     * @param inflater LayoutInflater to display layout of fragment
     * @param container ViewGroup base class for layout and view container
     * @param savedInstanceState Bundle environment data
     * @return this the View object for this fragment
     *
     * See: https://stackoverflow.com/questions/27425547/cannot-resolve-method-getsupportfragmentmanager-inside-fragment
     * See: https://stackoverflow.com/questions/7645880/listview-with-onitemclicklistener-android
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(getContext().getResources().getString(R.string.search_page_title));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        userListRecyclerView = view.findViewById(R.id.user_recyclerview);
        userListRecyclerView.setLayoutManager(linearLayoutManager);
        userListRecyclerView.addItemDecoration(new DividerItemDecoration(userListRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        userListAdapter = new UserListAdapter();
        userListRecyclerView.setAdapter(userListAdapter);
        userListAdapter.setOnClickListener(new UserListAdapter.onClickListener() {
            @Override
            public void onClick(@NonNull User user) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity)getActivity()).passDataToViewProfileActivity(user.getUserID());
                }
            }
        });

        searchUsersViewModel = ViewModelProviders.of(getActivity()).get(SearchUsersViewModel.class);
        searchUsersViewModel.getUserListLiveData().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users != null) {
                    userListAdapter.submitList(users);
                }
            }
        });

        return this.view;
    }

    /**
     * Create the search menu when activity is loaded
     * @param savedInstanceState Bundle environment data
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Override onCreateOptionsMenu method to create search menu
     * @param menu Menu search menu object
     * @param inflater MenuInflater to display search menu
     *
     * See: https://stackoverflow.com/questions/34603157/how-to-get-a-text-from-searchview
     * See: https://developer.android.com/reference/android/widget/SearchView
     * See: https://www.youtube.com/watch?v=_7B5iuyhIFk
     */
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_menu);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
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
                searchUsersViewModel.setUserNameQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Dynamically update search results as the user is typing
                searchUsersViewModel.setUserNameQuery(s);
                return false;
            }
        });
        sv.setIconifiedByDefault(false);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }
}

