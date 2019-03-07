package com.example.atheneum.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.BookInfoActivity;

/**
 * To hold all requests on a book
 *
 * @see BookInfoActivity
 */
public class BookRequestViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout requestItem;
    public TextView requesterNameTextView;

    public BookRequestViewHolder(@NonNull View bookView) {
        super(bookView);
        requestItem = (LinearLayout) bookView.findViewById(R.id.request_on_book_card);
        requesterNameTextView = (TextView) bookView.findViewById(R.id.requester_name);
    }
}
