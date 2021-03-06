package com.example.atheneum.views.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adds an offset to items in a RecyclerView. Used to create uniform spacing between elements when
 * using a RecyclerView with a GridLayout.
 * <p>
 * See: https://stackoverflow.com/a/30794046/11039833
 */
public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    /**
     * Instantiates a new Item offset decoration.
     *
     * @param itemOffset the item offset
     */
    public ItemOffsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    /**
     * Instantiates a new Item offset decoration.
     *
     * @param context      the context
     * @param itemOffsetId the item offset id
     */
    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}
