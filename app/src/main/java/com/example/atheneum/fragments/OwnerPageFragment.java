package com.example.atheneum.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.atheneum.R;
import com.example.atheneum.activities.AddEditBookActivity;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.views.adapters.OwnerBooksListAdapter;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.OwnerCollectionRefUtils;
import com.example.atheneum.viewmodels.OwnerBooksViewModel;
import com.example.atheneum.viewmodels.OwnerBooksViewModelFactory;
import com.example.atheneum.viewmodels.UserViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * The Owner page fragment that can be navigated to using the hamburger menu on the main pages
 * after user has logged in.
 *
 * See: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
 * See: https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
 * See: https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
 */
public class OwnerPageFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private RecyclerView ownerBooksRecyclerView;
    private OwnerBooksListAdapter ownerBooksListAdapter;
    private RecyclerView.LayoutManager ownerBooksLayoutManager;

    private Spinner ownBookSpinner;
    private ArrayAdapter<String> ownBookSpinnerAdapter;

    private static final String TAG = OwnerPageFragment.class.getSimpleName();

    public static final int REQUEST_DELETE_ENTRY = 1;


    /**
     * Instantiates a new Owner page fragment.
     */
    public OwnerPageFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_owner_page, container, false);

        this.context = getContext();

        //https://developer.android.com/guide/topics/ui/controls/spinner
        ownBookSpinner = (Spinner) this.view.findViewById(R.id.ownBookSpinner);
        ownBookSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ownBookSpinnerArray));
        ownBookSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownBookSpinner.setAdapter(ownBookSpinnerAdapter);


        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.owner_page_title));
        }

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            ownerBooksRecyclerView = (RecyclerView) this.view.findViewById(R.id.owner_books_recycler_view);
            ownerBooksRecyclerView.setHasFixedSize(true);
            ownerBooksLayoutManager = new LinearLayoutManager(this.context);
            ownerBooksRecyclerView.setLayoutManager(ownerBooksLayoutManager);
            ownerBooksRecyclerView.addItemDecoration(new DividerItemDecoration(ownerBooksRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));

            ownerBooksListAdapter = new OwnerBooksListAdapter();
            ownerBooksListAdapter.setBookItemOnClickListener(new OwnerBooksListAdapter.BookItemOnClickListener() {
                @Override
                public void onClick(View v, Book book) {
                    Log.i("OwnerBook", "clicked on a book");
                    Intent intent = new Intent(context, BookInfoActivity.class);
                    intent.putExtra("bookID", book.getBookID());
                    if (mainActivity != null) {
                        mainActivity.startActivityForResult(intent, REQUEST_DELETE_ENTRY);
                    }
                }
            });
            ownerBooksRecyclerView.setAdapter(ownerBooksListAdapter);

            OwnerBooksViewModel ownerBooksViewModel = ViewModelProviders
                    .of(getActivity(), new OwnerBooksViewModelFactory(firebaseUser.getUid()))
                    .get(OwnerBooksViewModel.class);
            ownerBooksViewModel.ownerBooksLiveData().observe(getActivity(), new Observer<ArrayList<Book>>() {
                @Override
                public void onChanged(@Nullable ArrayList<Book> ownerBooks) {
                    ownerBooksListAdapter.submitList(ownerBooks);
                }
            });

            FloatingActionButton fab = (FloatingActionButton) this.view.findViewById(R.id.add_book);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddEditBookActivity.class);
                    intent.putExtra("BookID", "");
                    startActivity(intent);
                }
            });
        } else {
            Log.w(TAG, "impossible!!!");
        }

        return this.view;
    }
}