package com.example.atheneum.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
    /**
     * The Request item.
     */
    public LinearLayout requestItem;
    /**
     * The Requester name text view.
     */
    public TextView requesterNameTextView;
    /**
     * The Decline request image view.
     */
    public ImageView declineRequestImageView;
    /**
     * The Accept request image view.
     */
    public ImageView acceptRequestImageView;

    /**
     * Instantiates a new Book request view holder.
     *
     * @param bookView the book view
     */
    public BookRequestViewHolder(@NonNull View bookView) {
        super(bookView);
        requestItem = (LinearLayout) bookView.findViewById(R.id.request_on_book_card);
        requesterNameTextView = (TextView) bookView.findViewById(R.id.requester_name);
        declineRequestImageView = (ImageView) bookView.findViewById(R.id.request_decline);
        acceptRequestImageView = (ImageView) bookView.findViewById(R.id.request_accept);
    }
}
