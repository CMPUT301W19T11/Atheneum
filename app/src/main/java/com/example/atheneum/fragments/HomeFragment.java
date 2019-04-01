package com.example.atheneum.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.viewmodels.RecommendedBooksViewModel;
import com.example.atheneum.viewmodels.RecommendedBooksViewModelFactory;
import com.example.atheneum.views.adapters.RecommendedBooksListAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass that displays the home page.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    /**
     * Instantiates a new Home fragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set title
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.app_name));
        }

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recommendedBooksRecyclerView = (RecyclerView) view.findViewById(R.id.popular_book_recommendations_recycler_view);
        recommendedBooksRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recommendedBooksRecyclerView.setLayoutManager(layoutManager);
        recommendedBooksRecyclerView.addItemDecoration(new DividerItemDecoration(recommendedBooksRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        final RecommendedBooksListAdapter recommendedBooksListAdapter = new RecommendedBooksListAdapter();
        recommendedBooksListAdapter.setBookItemOnClickListener(new RecommendedBooksListAdapter.BookItemOnClickListener() {
            @Override
            public void onClick(View v, Book book) {
                Log.i(TAG, "clicked on a book");
                Intent intent = new Intent(view.getContext(), BookInfoActivity.class);
                intent.putExtra("bookID", book.getBookID());
                intent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.BORROWER_VIEW);
                startActivity(intent);
            }
        });
        recommendedBooksRecyclerView.setAdapter(recommendedBooksListAdapter);

        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            String userID = FirebaseAuthUtils.getCurrentUser().getUid();
            RecommendedBooksViewModel recommendedBooksViewModel = ViewModelProviders
                    .of(this, new RecommendedBooksViewModelFactory(userID))
                    .get(RecommendedBooksViewModel.class);
            recommendedBooksViewModel
                    .getRecommendedBooksLiveData()
                    .observe(this, new Observer<List<Book>>() {
                        @Override
                        public void onChanged(@Nullable List<Book> books) {
                            if (books != null && !books.isEmpty()) {
                                for (Book book : books) {
                                    Log.i(TAG, "got recommended book: " + book);
                                }
                            }
                            recommendedBooksListAdapter.submitList(books);
                        }
                    });
        } else {
            Log.w(TAG, "if user is here, they should be authenticated!");
        }

        return view;
    }

}
