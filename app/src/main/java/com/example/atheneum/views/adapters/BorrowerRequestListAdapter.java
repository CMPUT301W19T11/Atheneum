package com.example.atheneum.views.adapters;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.example.atheneum.viewmodels.BookInfoViewModel;
import com.example.atheneum.viewmodels.BookInfoViewModelFactory;
import com.example.atheneum.viewmodels.BorrowerRequestsViewModel;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;

import org.w3c.dom.Text;

public class BorrowerRequestListAdapter extends ListAdapter<Request, BorrowerRequestListAdapter.ViewHolder> {
    private static final String TAG = BorrowerRequestListAdapter.class.getSimpleName();
    private static final DiffUtil.ItemCallback<Request> DIFF_CALLBACK = new DiffUtil.ItemCallback<Request>() {
        @Override
        public boolean areItemsTheSame(@NonNull Request Request, @NonNull Request other) {
            return Request.getRequesterID().equals(other.getRequesterID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Request Request, @NonNull Request other) {
            return Request.equals(other);
        }
    };

    public interface OnRequestItemClickListener {
        void onClick(View v, Request request);
    }

    /**
     * Custom View Holder used to display the user and deal with onClick events.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bookTitle;
        private TextView bookOwner;
        private TextView requestStatus;

        /**
         * Create view holder object
         *
         * @param view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            bookTitle = view.findViewById(R.id.show_title);
            bookOwner = view.findViewById(R.id.show_owner);
            requestStatus = view.findViewById(R.id.rStatus);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRequestItemClickListener != null) {
                        onRequestItemClickListener.onClick(v, getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }

    private OnRequestItemClickListener onRequestItemClickListener;

    /**
     * Create a new instance of UserListAdapter
     */
    public BorrowerRequestListAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnRequestItemClickListener(OnRequestItemClickListener onRequestItemClickListener) {
        this.onRequestItemClickListener = onRequestItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Request request = getItem(position);
        viewHolder.requestStatus.setText(request.getrStatus().toString());
        final String bookID = request.getBookID();

        if (request.getrStatus().equals(Request.Status.PENDING)) {
            viewHolder.requestStatus.setTextColor(Color.MAGENTA);
        } else if (request.getrStatus().equals(Request.Status.DECLINED)) {
            viewHolder.requestStatus.setTextColor(Color.RED);
        } else if (request.getrStatus().equals(Request.Status.ACCEPTED)) {
            viewHolder.requestStatus.setTextColor(Color.BLUE);
        }

        if (viewHolder.itemView.getContext() instanceof FragmentActivity) {
            if (bookID != null && !bookID.equals("")) {
                final FragmentActivity fragmentActivity = (FragmentActivity) (viewHolder.itemView.getContext());
                // Have multiple instances of the UserViewModel per activity
                String providerKey = BookInfoViewModel.generateViewModelProviderKey(bookID);
                BookInfoViewModel borrowerViewModel = ViewModelProviders
                        .of(fragmentActivity, new BookInfoViewModelFactory(bookID))
                        .get(providerKey, BookInfoViewModel.class);
                borrowerViewModel.getBookLiveData().observe(fragmentActivity, new Observer<Book>() {
                    @Override
                    public void onChanged(@Nullable Book book) {
                        if (book != null) {
                            viewHolder.bookTitle.setText(book.getTitle());
                            String providerKey = UserViewModel.generateViewModelProviderKey(book.getOwnerID());
                            if (book.getOwnerID() != null && !book.getOwnerID().equals("")) {
                                // This is not ideal, should be changed in the future
                                UserViewModel userViewModel = ViewModelProviders
                                        .of(fragmentActivity, new UserViewModelFactory(book.getOwnerID()))
                                        .get(providerKey, UserViewModel.class);
                                userViewModel.getUserLiveData().observe(fragmentActivity, new Observer<User>() {
                                    @Override
                                    public void onChanged(@Nullable User user) {
                                        if (user != null) {
                                            viewHolder.bookOwner.setText(user.getUserName());
                                        }
                                    }
                                });
                            } else {
                                Log.w(TAG, "impossible!");
                                viewHolder.bookOwner.setText("None");
                            }
                        }
                    }
                });
            }
        }
    }
}
