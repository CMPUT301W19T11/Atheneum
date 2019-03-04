package com.example.atheneum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.models.Book;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Bind Measurements to OwnerBooksRecyclerView in MainActivity
 *
 * See: https://developer.android.com/guide/topics/ui/layout/recyclerview#java
 * See: https://www.androidhive.info/2016/01/android-working-with-recycler-view/
 *
 * @author marcus
 * @version 1.0
 * @since 2019-01-27
 */
public class OwnerBooksAdapter extends
        RecyclerView.Adapter<OwnerBooksAdapter.MeasurementViewHolder> {
    private ArrayList<Book> ownerBooks;
    private OwnerBooksAdapter activity = this;
    private Context mContext;
    private Activity mActivity;
    public static final int REQUEST_DELETE_ENTRY = 1;

    /**
     * The books view holder.
     */
    public static class MeasurementViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout bookItem;

        public TextView titleTextView;
        public TextView authorTextView;
        public TextView statusTextView;



        /**
         * Instantiates a new books view holder.
         *
         * @param view
         */
        public MeasurementViewHolder(View view) {
            super(view);
            bookItem = (LinearLayout) view.findViewById(R.id.book_card);
            titleTextView = (TextView) view.findViewById(R.id.book_title);
            authorTextView = (TextView) view.findViewById(R.id.book_author);
            statusTextView = (TextView) view.findViewById(R.id.book_status);

        }
    }

    /**
     * Instantiates a new Book adapter.
     *
     * @param ownerBooks
     */
    public OwnerBooksAdapter(ArrayList<Book> ownerBooks, Context context) {
        this.ownerBooks = ownerBooks;
        this.mContext = context;
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public OwnerBooksAdapter.MeasurementViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_card, parent, false);
        final MeasurementViewHolder vh = new MeasurementViewHolder(v);

        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MeasurementViewHolder holder, final int position)  {
        holder.titleTextView.setText(
                ownerBooks.get(position).getTitle());
        holder.authorTextView.setText(
                ownerBooks.get(position).getAuthor());
        holder.statusTextView.setText(
                ownerBooks.get(position).getStatus().toString());

        holder.bookItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
//                Toast.makeText(parent.getContext(), "Test Click" + String.valueOf(vh.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                Log.i("OwnerBook", "clicked on a book");
                String bookID = ownerBooks.get(holder.getAdapterPosition()).getBookID();
                Intent intent = new Intent(mContext, BookInfoActivity.class);
                intent.putExtra("bookID", bookID);
                //intent.putExtra("position", position);
                mContext.startActivity(intent);
                //mActivity.startActivityForResult(intent, REQUEST_DELETE_ENTRY);

            }
        });

    }

    /**
     * Get the number of measurements
     *
     * @return measurements.size()
     */
    @Override
    public int getItemCount() {
        return ownerBooks.size();
    }
}