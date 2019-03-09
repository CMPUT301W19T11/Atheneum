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
    public TextView borrowerNameTextView;

    /**
     * Instantiates a new books view holder.
     *
     * @param view
     */
    public BookViewHolder(View view) {
        super(view);
        bookItem = (LinearLayout) view.findViewById(R.id.book_card);
        titleTextView = (TextView) view.findViewById(R.id.book_title);
        authorTextView = (TextView) view.findViewById(R.id.book_author);
        statusTextView = (TextView) view.findViewById(R.id.book_status);
        borrowerNameTextView = (TextView) view.findViewById(R.id.bookcard_borrowername);
    }
}

