package com.example.atheneum.views.adapters;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
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
import com.example.atheneum.models.Notification;
import com.example.atheneum.viewmodels.NotificationsViewModel;

import java.util.List;

/**
 * Adapter for notifications RecyclerView
 *
 * See: https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
 */
public class NotificationListAdapter extends ListAdapter<Notification, NotificationListAdapter.ViewHolder> {
    private final static String TAG = NotificationListAdapter.class.getSimpleName();
    private final Context mContext;
    private NotificationsViewModel mNotificationsViewModel;

    public static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Notification>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Notification notification, @NonNull Notification other) {
                    return notification.getNotificationID().equals(other.getNotificationID());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Notification notification, @NonNull Notification other) {
                    return notification.equals(other);
                }
            };

    public NotificationListAdapter(Context context)
    {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setNotificationsViewModel(NotificationsViewModel notificationsViewModel) {
        mNotificationsViewModel = notificationsViewModel;
    }

    /**
     * Custon ViewHolder for notification
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout notificationCard;
        private TextView message;
        private TextView time;

        /**
         * Create view holder object
         *
         * @param view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            notificationCard = (LinearLayout) view.findViewById(R.id.notification_card);
            message = (TextView) view.findViewById(R.id.notification_message);
            time = (TextView) view.findViewById(R.id.notification_time);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Notification notification = getItem(getAdapterPosition());
                    if (onClickListener != null) {
                        onClickListener.onClick(notification);
                    }
                }
            });
        }
    }

    /**
     * Provide interface to handle onClick events for any Notification in the list
     */
    public interface onClickListener {
        public void onClick(@NonNull Notification notification);
    }

    private NotificationListAdapter.onClickListener onClickListener;

    public void setOnClickListener(NotificationListAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Set up ViewHolder to contain notification
     *
     * @param viewGroup
     * @param position
     * @return ViewHolder containing the notification view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notification_card, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Binds Notification object to an instance of the ViewHolder
     *
     * @param holder ViewHolder object
     * @param position Position of Notification within the list.
     *                 Notification object will be bound to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = getItem(position);
        holder.message.setText(notification.getMessage());
        // Formats Date into 'how long ago'
        // See: https://stackoverflow.com/a/3859298
        // See: https://developer.android.com/reference/android/text/format/DateUtils.html#getRelativeTimeSpanString%28long%29
        CharSequence momentsAgo = android.text.format.DateUtils
                .getRelativeTimeSpanString(notification.getCreationDate().getTime());
        holder.time.setText(momentsAgo);

        if (notification.getIsSeen()) {
            holder.notificationCard
                    .setBackgroundColor(mContext.getResources().getColor(R.color.notificationSeen));
        } else {
            holder.notificationCard
                    .setBackgroundColor(mContext.getResources().getColor(R.color.notificationNotSeen));
        }
    }

    /**
     * Delete notification from list
     *
     * @param position
     */
    public void deleteItem(int position) {
        Notification deletedNotification = getItem(position);
        Log.i(TAG, "SWIPED RIGHT, DELETE ITEM: " + position);
        mNotificationsViewModel.deleteNotification(deletedNotification);
    }

    /**
     * Make notification in list seen
     */
    public void makeItemSeen(int position) {
        Notification seenNotification = getItem(position);
        Log.i(TAG, "SWIPED LEFT, SEEN ITEM: " + position);
        mNotificationsViewModel.makeNotificationSeen(seenNotification);
        if (seenNotification.getIsSeen()) {
            notifyDataSetChanged();
        }
    }
}
