package com.example.atheneum.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.atheneum.R;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Photo;
import com.example.atheneum.utils.PhotoUtils;

/**
 * Adapter that displays a list of photos for a book to a Recyclerview.
 * <p>
 * See: https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
 */
public class BookPhotosListAdapter extends ListAdapter<Photo, BookPhotosListAdapter.ViewHolder> {
    private static final String TAG = BookPhotosListAdapter.class.getSimpleName();
    private static final DiffUtil.ItemCallback<Photo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Photo photo, @NonNull Photo otherPhoto) {
            return photo.getPhotoID().equals(otherPhoto.getPhotoID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Photo photo, @NonNull Photo otherPhoto) {
            return photo.equals(otherPhoto);
        }
    };

    /**
     * Provides an interface of callbacks to handle click and long click events on the Photo
     * within the list.
     */
    public interface OnItemClickListener {
        /**
         * Handles click events for a particular photo within the list.
         *
         * @param v     ImageView containing the photo.
         * @param photo Photo object from the list.
         */
        public void onClick(View v, Photo photo);

        /**
         * Handles long click events for a particular photo within the list.
         *
         * @param v     ImageView containing the photo.
         * @param photo Photo object from the list.
         */
        public void onLongClick(View v, Photo photo);
    }

    /**
     * Custom View Holder used to display the Photo and deal with click and long click events.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = BookPhotosListAdapter.TAG + ":" + ViewHolder.class.getSimpleName();
        private ImageView bookPhoto;

        /**
         * Create view holder object
         *
         * @param itemView the item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookPhoto = itemView.findViewById(R.id.book_gallery_photo);
            bookPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "bookPhoto.onClick()");
                    Photo photo = getItem(getAdapterPosition());
                    if (onItemClickListener != null) {
                        Log.i(TAG, "onItemClickListener.onClick(photo)");
                        onItemClickListener.onClick(v, photo);
                    }
                }
            });
            bookPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Photo photo = getItem(getAdapterPosition());
                    if (onItemClickListener != null) {
                        onItemClickListener.onLongClick(v, photo);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Sets listener(s) for click and long click events for a photo in the list.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    /**
     * Create a new instance of the adapter.
     */
    public BookPhotosListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Creates the viewholder from the RecyclerView item layout.
     *
     * @param viewGroup
     * @param position
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.book_photos_gallery_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Binds Photo object to an instance of the ViewHolder.
     *
     * @param viewHolder ViewHolder object
     * @param position Position of Photo within the list. Photo object will be bound to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String photoBase64 = getItem(position).getEncodedString();
        // See: https://stackoverflow.com/a/34702983/11039833
        // SEe: https://stackoverflow.com/a/52065027/11039833
        Glide.with(viewHolder.itemView.getContext())
                .load(Photo.DecodeBase64BitmapPhoto(photoBase64))
                .apply(new RequestOptions()
                            .centerCrop()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                .into(viewHolder.bookPhoto);
    }

}
