package com.example.atheneum.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.atheneum.R;
import com.example.atheneum.models.Notification;
import com.example.atheneum.viewmodels.NotificationsViewModel;
import com.example.atheneum.views.adapters.NotificationListAdapter;

/**
 * Callback for swiping items to delete in a RecyclerView
 * Used in NotificationsActivity
 * <p>
 * See: https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private NotificationListAdapter mAdapter;
    private Drawable deleteIcon;
    private Drawable seenIcon;
    private final ColorDrawable background;

    /**
     * Instantiates a new Swipe to delete callback.
     *
     * @param adapter the adapter
     */
    public SwipeToDeleteCallback(NotificationListAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        deleteIcon = ContextCompat.getDrawable(mAdapter.getContext(),
                R.drawable.close);
        seenIcon = ContextCompat.getDrawable(mAdapter.getContext(),
                R.drawable.check);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            mAdapter.makeItemSeen(position);
        }
        else if (direction == ItemTouchHelper.RIGHT) {
            mAdapter.deleteItem(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            deleteIcon.setBounds(iconRight, iconTop, iconLeft, iconBottom);

            background.setColor(Color.RED);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
            background.draw(c);
            deleteIcon.draw(c);

        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - seenIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            seenIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setColor(Color.GREEN);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
            seenIcon.draw(c);
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
            background.draw(c);
        }
    }
}