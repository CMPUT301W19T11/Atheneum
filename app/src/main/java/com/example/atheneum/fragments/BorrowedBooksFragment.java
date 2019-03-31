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

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.AddEditBookActivity;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.viewmodels.BorrowerBooksViewModel;
import com.example.atheneum.viewmodels.BorrowerBooksViewModelFactory;
import com.example.atheneum.views.adapters.OwnerBooksListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.atheneum.fragments.OwnerPageFragment.REQUEST_DELETE_ENTRY;

/**
 *
 */
public class BorrowedBooksFragment extends Fragment {

    private View view;
    private MainActivity mainActivity = null;
    private Context context;
    private FloatingActionButton addBook;

    private RecyclerView borrowerBooksRecyclerView;
    private OwnerBooksListAdapter borrowerBooksListAdapter;
    private RecyclerView.LayoutManager borrowerBooksLayoutManager;
    private Spinner borrowedBookSpinner;

    private static final String TAG = BorrowedBooksFragment.class.getSimpleName();


    public BorrowedBooksFragment() {
        // Required empty public constructor
    }




    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_owner_page, container, false);

        this.context = getContext();

        borrowedBookSpinner = (Spinner) this.view.findViewById(R.id.ownBookSpinner);
        borrowedBookSpinner.setVisibility(View.GONE);

        addBook = (FloatingActionButton) this.view.findViewById(R.id.add_book);
        addBook.setVisibility(View.GONE);

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
//            mainActivity.setActionBarTitle(context.getResources().getString(R.string.owner_page_title));
        }

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            borrowerBooksRecyclerView = (RecyclerView) this.view.findViewById(R.id.owner_books_recycler_view);
            borrowerBooksRecyclerView.setHasFixedSize(true);
            borrowerBooksLayoutManager = new LinearLayoutManager(this.context);
            borrowerBooksRecyclerView.setLayoutManager(borrowerBooksLayoutManager);
            borrowerBooksRecyclerView.addItemDecoration(new DividerItemDecoration(borrowerBooksRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));

            borrowerBooksListAdapter = new OwnerBooksListAdapter();
            borrowerBooksListAdapter.setBookItemOnClickListener(new OwnerBooksListAdapter.BookItemOnClickListener() {
                @Override
                public void onClick(View v, Book book) {
                    Log.i("Borrowed Book", "clicked on a book");
                    Intent intent = new Intent(context, BookInfoActivity.class);
                    intent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.REQUSET_VIEW);
                    intent.putExtra(BookInfoActivity.BOOK_ID, book.getBookID());
                    if (mainActivity != null) {
                        mainActivity.startActivityForResult(intent, REQUEST_DELETE_ENTRY);
                    }
                }
            });
            borrowerBooksRecyclerView.setAdapter(borrowerBooksListAdapter);

            BorrowerBooksViewModel borrowerBooksViewModel = ViewModelProviders
                .of(getActivity(), new BorrowerBooksViewModelFactory(firebaseUser.getUid()))
                .get(BorrowerBooksViewModel.class);
            borrowerBooksViewModel.borrowerBooksLiveData().observe(getActivity(), new Observer<ArrayList<Book>>() {
                @Override
                public void onChanged(@Nullable ArrayList<Book> borrowedBooks) {
                    ArrayList<Book> submittedBorrowedBook = new ArrayList<Book>();
                    for(int i=0; i<borrowedBooks.size(); i++){
                        Book book = borrowedBooks.get(i);
                        if(book.getStatus().toString().equals("BORROWED")){
                            submittedBorrowedBook.add(book);
                        }
                    }
                    borrowerBooksListAdapter.submitList(submittedBorrowedBook);
                }
            });

        } else {
            Log.w(TAG, "impossible!!!");
        }

        return this.view;

    }

}
