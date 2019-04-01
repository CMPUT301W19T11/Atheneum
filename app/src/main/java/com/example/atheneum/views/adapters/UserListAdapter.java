package com.example.atheneum.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.models.User;

/**
 * Adapter that displays a list of users to a Recyclerview.
 * <p>
 * See: https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
 */
public class UserListAdapter extends ListAdapter<User, UserListAdapter.ViewHolder> {
    // Class that is used to compare elements of new list to feed into adapter in the background
    // thread.
    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User user, @NonNull User other) {
            return user.getUserID().equals(other.getUserID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User user, @NonNull User other) {
            return user.equals(other);
        }
    };

    /**
     * Custom View Holder used to display the user and deal with onClick events.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;

        /**
         * Create view holder object
         *
         * @param view the view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            userName = (TextView)view.findViewById(R.id.userName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = getItem(getAdapterPosition());
                    if (onClickListener != null) {
                        onClickListener.onClick(user);
                    }
                }
            });
        }
    }

    /**
     * Provides an interface to handle onClick events on a User in the list
     */
    public interface onClickListener {
        /**
         * Handle onClick event on a User within the list
         *
         * @param user User obtained from the list contained within the Adapter
         */
        public void onClick(@NonNull User user);
    }

    private UserListAdapter.onClickListener onClickListener;

    /**
     * Create a new instance of UserListAdapter
     */
    public UserListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Changes the on click listener
     *
     * @param onClickListener New on click listener
     */
    public void setOnClickListener(UserListAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     *
     *
     * @param viewGroup
     * @param position
     * @return viewholder for this adapter
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.search_users_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Binds User object to an instance of the ViewHolder.
     *
     * @param holder ViewHolder object
     * @param position Position of User within the list. User object will be bound to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);
        holder.userName.setText(user.getUserName());
    }
}
