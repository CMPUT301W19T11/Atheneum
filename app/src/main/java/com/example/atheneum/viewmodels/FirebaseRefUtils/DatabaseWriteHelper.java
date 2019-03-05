package com.example.atheneum.viewmodels.FirebaseRefUtils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class DatabaseWriteHelper {
    private static final String TAG = DatabaseWriteHelper.class.getSimpleName();

    public static void addNewBook(User owner, Book newBook) {
        HashMap<String, Object> updates = new HashMap<String, Object>();
        // Atomically update multiple database paths at once by using a multi-path update
        // Note: Can't use the DatabaseReference objects and then convert to a key, must use formatted
        // strings when doing multi-path updates
        final String bookRef = String.format("books/%s", newBook.getBookID());
        final String ownerBookRef = String.format("ownerCollection/%s/%s", owner.getUserID(), newBook.getBookID());
        updates.put(bookRef, newBook);
        // Even though we are just interested in the bookID, we have store a value for the bookID
        // for Firebase. So we just use true.
        updates.put(ownerBookRef, true);
        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRef: " + bookRef.toString());
                    Log.i(TAG, "ownerBookRef: " + ownerBookRef.toString());
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }
}
