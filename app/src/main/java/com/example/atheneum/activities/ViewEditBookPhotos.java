package com.example.atheneum.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.atheneum.R;
import com.example.atheneum.utils.CameraHandler;
import com.example.atheneum.models.Photo;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.viewmodels.BookPhotosViewModel;
import com.example.atheneum.viewmodels.BookPhotosViewModelFactory;
import com.example.atheneum.views.adapters.BookPhotosListAdapter;
import com.example.atheneum.views.adapters.ItemOffsetDecoration;

import java.util.ArrayList;

/**
 * Activity used to perform CRUD operations on a list of photos associated with a particular book.
 */
public class ViewEditBookPhotos extends AppCompatActivity {
    private static final String TAG = ViewEditBookPhotos.class.getSimpleName();
    public static final String INTENT_BOOK_ID =
            ViewEditBookPhotos.class.getCanonicalName() + ":BOOK_ID";
    public static final String INTENT_OWNER_USER_ID =
            ViewEditBookPhotos.class.getCanonicalName() + ":OWNER_USER_ID";

    private CameraHandler cameraHandler;
    private BookPhotosViewModel bookPhotosViewModel;
    private FloatingActionButton fab;
    private BookPhotosListAdapter bookPhotosListAdapter;
    private RecyclerView bookPhotosGalleryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_book_photos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String bookID = getIntent().getStringExtra(INTENT_BOOK_ID);
        final String ownerUserID = getIntent().getStringExtra(INTENT_OWNER_USER_ID);

        if (bookID == null || bookID.equals("")) {
            Log.w(TAG, "bookID is not valid! This should never happen!");
            return;
        } else if (ownerUserID == null || ownerUserID.equals("")) {
            Log.w(TAG, "ownerUserID is not valid! This should never happen!");
            return;
        } else if (!FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            Log.w(TAG, "user is not authenticated! This should not happen!");
            return;
        }

        bookPhotosListAdapter = new BookPhotosListAdapter();
        bookPhotosGalleryRecyclerView = findViewById(R.id.book_photos_gallery);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        bookPhotosGalleryRecyclerView.addItemDecoration(itemDecoration);
        bookPhotosGalleryRecyclerView.setAdapter(bookPhotosListAdapter);
        bookPhotosGalleryRecyclerView.setLayoutManager(layoutManager);

        Log.i(TAG, "bookID: " + bookID);
        BookPhotosViewModelFactory factory = new BookPhotosViewModelFactory(bookID);
        bookPhotosViewModel = ViewModelProviders.of(this, factory).get(BookPhotosViewModel.class);
        bookPhotosViewModel.getPhotosLiveData().observe(this, new Observer<ArrayList<Photo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Photo> photos) {
                bookPhotosListAdapter.submitList(photos);
            }
        });

        if (FirebaseAuthUtils.getCurrentUser().getUid().equals(ownerUserID)) {
            setTitle(R.string.activity_title_edit_book_photos);

            cameraHandler = CameraHandler.newInstance(this);

            bookPhotosListAdapter.setOnItemClickListener(new BookPhotosListAdapter.OnItemClickListener() {
                @Override
                public void onClick(final View v, final Photo photo) {
                    // Code gotten from https://stackoverflow.com/a/33650105
                    // License: https://creativecommons.org/licenses/by-sa/3.0/
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(getString(R.string.book_photo_dialog_edit_prompt))
                            .setPositiveButton(getString(R.string.dialog_edit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cameraHandler.dispatchTakePictureIntent();
                                    cameraHandler.setPictureTakenListener(new CameraHandler.OnPictureTakenListener() {
                                        @Override
                                        public void onPictureTaken(Bitmap bitmap) {
                                            bookPhotosViewModel.updatePhoto(photo, bitmap);
                                            cameraHandler.removePictureTakenListener();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do Nothing
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public void onLongClick(View v, final Photo photo) {
                    // Code gotten from https://stackoverflow.com/a/33650105
                    // License: https://creativecommons.org/licenses/by-sa/3.0/
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(getString(R.string.book_photo_dialog_delete_prompt))
                            .setPositiveButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bookPhotosViewModel.deletePhoto(photo);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do Nothing
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    cameraHandler.dispatchTakePictureIntent();
                    cameraHandler.setPictureTakenListener(new CameraHandler.OnPictureTakenListener() {
                        @Override
                        public void onPictureTaken(Bitmap bitmap) {
                            bookPhotosViewModel.addPhoto(bitmap);
                            cameraHandler.removePictureTakenListener();
                        }
                    });
                }
            });
        } else {
            setTitle(R.string.activity_title_view_book_photos);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (cameraHandler != null) {
            cameraHandler.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (cameraHandler != null) {
            cameraHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
