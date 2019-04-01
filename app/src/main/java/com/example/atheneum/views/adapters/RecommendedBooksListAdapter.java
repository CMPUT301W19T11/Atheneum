package com.example.atheneum.views.adapters;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;

/**
 * Adapter that displays a list of books owned by a user to a RecyclerView.
 */
public class RecommendedBooksListAdapter extends ListAdapter<Book, RecommendedBooksListAdapter.ViewHolder> {
    private static final String TAG = OwnerBooksListAdapter.class.getSimpleName();
    private static final DiffUtil.ItemCallback<Book> DIFF_CALLBACK = new DiffUtil.ItemCallback<Book>() {
        @Override
        public boolean areItemsTheSame(@NonNull Book book, @NonNull Book otherBook) {
            return book.getBookID().equals(otherBook.getBookID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Book book, @NonNull Book otherBook) {
            return book.equals(otherBook);
        }
    };

    /**
     * Custom View Holder used to display the book and deal with onClick events.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Book item.
         */
        public LinearLayout bookItem;
        /**
         * The Title text view.
         */
        public TextView titleTextView;
        /**
         * The Author text view.
         */
        public TextView authorTextView;
        /**
         * The Status text view.
         */
        public TextView statusTextView;
        /**
         * The Owner name text view.
         */
        public TextView ownerNameTextView;

        /**
         * Instantiates a new books view holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            bookItem = (LinearLayout) view.findViewById(R.id.book_card);
            titleTextView = (TextView) view.findViewById(R.id.book_title);
            authorTextView = (TextView) view.findViewById(R.id.book_author);
            statusTextView = (TextView) view.findViewById(R.id.book_status);
            ownerNameTextView = (TextView) view.findViewById(R.id.bookcard_ownername);
        }
    }

    /**
     * Provides an interface to handle onClick events on a book in the list
     */
    public interface BookItemOnClickListener {
        /**
         * Handles onClick events on books in the list.
         *
         * @param v    View that was clicked.
         * @param book Book bound to the view.
         */
        void onClick(View v, Book book);
    }

    private BookItemOnClickListener bookItemOnClickListener;

    /**
     * Create a new instance of OwnerBooksListAdapter
     */
    public RecommendedBooksListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Changes the onClick listener for book clicks
     *
     * @param bookItemOnClickListener New on click listener
     */
    public void setBookItemOnClickListener(BookItemOnClickListener bookItemOnClickListener) {
        this.bookItemOnClickListener = bookItemOnClickListener;
    }

    /**
     * Creates new View Holder
     *
     * @param viewGroup ViewGroup to inflate view holder.
     * @param i Position
     * @return New instance of ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recommended_book_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind a book to the view holder
     *
     * @param holder View holder to bind book to.
     * @param position Position of book within list.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "in onBindViewHolder!");

        final Book book = getItem(position);

        holder.titleTextView.setText(
                book.getTitle());
        holder.authorTextView.setText(
                book.getAuthor());
        holder.statusTextView.setText(
                book.getStatus().toString());

        // change color of status text
        Book.Status bookStatus = book.getStatus();
        if (bookStatus == Book.Status.ACCEPTED) {
            holder.statusTextView
                    .setTextColor(holder.itemView.getResources().getColor(R.color.bookAccepted));
        } else if (bookStatus == Book.Status.AVAILABLE) {
            holder.statusTextView
                    .setTextColor(holder.itemView.getResources().getColor(R.color.bookAvailable));
        } else if (bookStatus == Book.Status.REQUESTED) {
            holder.statusTextView
                    .setTextColor(holder.itemView.getResources().getColor(R.color.bookRequested));
        } else if (bookStatus == Book.Status.BORROWED) {
            holder.statusTextView
                    .setTextColor(holder.itemView.getResources().getColor(R.color.bookBorrowed));
        }

        if (holder.itemView.getContext() instanceof FragmentActivity) {
            if (!(book.getOwnerID() == null || book.getOwnerID().equals(""))) {
                FragmentActivity fragmentActivity = (FragmentActivity) (holder.itemView.getContext());
                // Have multiple instances of the UserViewModel per activity
                String providerKey = UserViewModel.generateViewModelProviderKey(book.getOwnerID());
                UserViewModel ownerViewModel = ViewModelProviders
                        .of(fragmentActivity, new UserViewModelFactory(book.getOwnerID()))
                        .get(providerKey, UserViewModel.class);
                ownerViewModel.getUserLiveData().observe(fragmentActivity, new Observer<User>() {
                    @Override
                    public void onChanged(@Nullable User owner) {
                        if (owner != null) {
                            holder.ownerNameTextView.setText(owner.getUserName());
                        } else {
                            Log.w(TAG, "nonexistent user error, shouldn't be here");
                        }
                    }
                });
            } else {
                holder.ownerNameTextView.setText("None");
            }
        } else {
            holder.ownerNameTextView.setText("None");
            Log.w(TAG, "impossible condition!");
        }

        holder.bookItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookItemOnClickListener != null) {
                    bookItemOnClickListener.onClick(v, book);
                }
            }
        });
    }
}
