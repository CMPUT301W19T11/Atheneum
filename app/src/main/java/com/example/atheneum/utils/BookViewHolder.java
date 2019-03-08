package com.example.atheneum.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;

/**
 * The books view holder.
 */
public class BookViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout bookItem;
    public TextView titleTextView;
    public TextView authorTextView;
    public TextView statusTextView;
    private TextView borrowerNameTextView;
    private UserViewModel userViewModel;
    private View view;


    /**
     * Instantiates a new books view holder.
     *
     * @param view
     */
    public BookViewHolder(View view) {
        super(view);
        this.view = view;
        bookItem = (LinearLayout) view.findViewById(R.id.book_card);
        titleTextView = (TextView) view.findViewById(R.id.book_title);
        authorTextView = (TextView) view.findViewById(R.id.book_author);
        statusTextView = (TextView) view.findViewById(R.id.book_status);
        borrowerNameTextView = (TextView) view.findViewById(R.id.book_borrower_name);
    }

    /**
     * Given a string borrower ID, show the borrower's username(email), or None,
     * if no one is currently borrowing the book.
     *
     * @param borrowerEmail a String of the user's email, or "None"
     */
    public void setBorrowerEmail(String borrowerEmail) {
        this.borrowerNameTextView.setText(borrowerEmail);
    }
}

