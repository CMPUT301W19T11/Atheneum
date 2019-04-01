package com.example.atheneum.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.viewmodels.RecommendedBooksViewModel;
import com.example.atheneum.viewmodels.RecommendedBooksViewModelFactory;
import com.example.atheneum.views.adapters.RecommendedBooksListAdapter;

import java.util.List;

/**
 * The Recommended books activity.
 * handles showing recommended books to users
 * Shows targeted recommendations to the user after they return a book.
 */
public class RecommendedBooksActivity extends AppCompatActivity {
    private static final String TAG = RecommendedBooksActivity.class.getSimpleName();
    /**
     * The constant INTENT_KEY_BORROWER_ID.
     */
    public static final String INTENT_KEY_BORROWER_ID = RecommendedBooksActivity.class.getCanonicalName()
                                                        + ":BORROWER_ID";
    /**
     * The constant INTENT_KEY_ISBN.
     */
    public static final String INTENT_KEY_ISBN = RecommendedBooksActivity.class.getCanonicalName()
                                                    + ":ISBN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_books);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long isbn = getIntent().getLongExtra(INTENT_KEY_ISBN, Book.INVALILD_ISBN);
        String borrowerID = getIntent().getStringExtra(INTENT_KEY_BORROWER_ID);

        if (isbn == Book.INVALILD_ISBN || borrowerID == null || borrowerID.equals("")) {
            Log.w(TAG, "invalid data given to activity!");
            Snackbar.make(findViewById(R.id.recommended_books_constraint_layout), "Something went wrong!", Snackbar.LENGTH_LONG).show();
            return;
        }

        Log.i(TAG, "isbn: " + isbn);
        Log.i(TAG, "borrowerID: " + borrowerID);

        RecyclerView recommendedBooksRecyclerView = (RecyclerView) findViewById(R.id.recommended_books_recycler_view);
        recommendedBooksRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recommendedBooksRecyclerView.setLayoutManager(layoutManager);
        recommendedBooksRecyclerView.addItemDecoration(new DividerItemDecoration(recommendedBooksRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        final RecommendedBooksListAdapter recommendedBooksListAdapter = new RecommendedBooksListAdapter();
        recommendedBooksListAdapter.setBookItemOnClickListener(new RecommendedBooksListAdapter.BookItemOnClickListener() {
            @Override
            public void onClick(View v, Book book) {
                Log.i(TAG, "clicked on a book");
                Intent intent = new Intent(getApplicationContext(), BookInfoActivity.class);
                intent.putExtra("bookID", book.getBookID());
                intent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.BORROWER_VIEW);
                startActivity(intent);
            }
        });
        recommendedBooksRecyclerView.setAdapter(recommendedBooksListAdapter);

        RecommendedBooksViewModel recommendedBooksViewModel = ViewModelProviders
                .of(this, new RecommendedBooksViewModelFactory(borrowerID, isbn))
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

    }

}
