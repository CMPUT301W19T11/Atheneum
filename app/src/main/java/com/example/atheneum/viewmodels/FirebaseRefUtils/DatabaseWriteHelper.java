package com.example.atheneum.viewmodels.FirebaseRefUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Notification;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

    public static void deleteBook(String ownerUserID, String bookID) {
        HashMap<String, Object> updates = new HashMap<String, Object>();
        // Atomically update multiple database paths at once by using a multi-path update
        // Note: Can't use the DatabaseReference objects and then convert to a key, must use formatted
        // strings when doing multi-path updates
        final String bookRef = String.format("books/%s", bookID);
        final String ownerBookRef = String.format("ownerCollection/%s/%s", ownerUserID, bookID);
        // Add null to a node to delete it
        updates.put(bookRef, null);
        updates.put(ownerBookRef, null);
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

    public static void deleteBook(User owner, Book book) {
        deleteBook(owner.getUserID(), book.getBookID());
    }

    public static void updateBook(Book book) {
        BooksRefUtils.getBookRef(book).setValue(book);
    }

    public static void makeRequest(Request request, Notification notification) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String requesterRef = String.format("requestCollection/%s/%s",
                request.getRequesterID(),
                request.getBookID());
        final String bookRequestRef = String.format("bookRequests/%s/%s",
                request.getBookID(),
                request.getRequesterID());
        final String notificationsRef = String.format("notifications/%s/%s",
                notification.getNotificationReceiverID(),
                notification.getNotificationID());

        updates.put(requesterRef, request);
        updates.put(bookRequestRef, true);
        updates.put(notificationsRef, notification);
        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRequestRef: " + bookRequestRef.toString());
                    Log.i(TAG, "requesterRef: " + requesterRef.toString());
                    Log.i(TAG, "notificationRef: " + notificationsRef.toString());
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    public static void acceptRequest(final Request request,
                                     Notification acceptNotification,
                                     final Notification declineNotification) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String requesterRef = String.format("requestCollection/%s/%s",
                request.getRequesterID(),
                request.getBookID());
        final String bookRequestRef = String.format("bookRequests/%s/%s",
                request.getBookID(),
                request.getRequesterID());
        final String bookBorrowerIDRef = String.format("books/%s/borrowerID",
                request.getBookID());
        final String bookStatusRef = String.format("books/%s/status", request.getBookID());
        final String notificationsRef = String.format("notifications/%s/%s",
                acceptNotification.getNotificationReceiverID(),
                acceptNotification.getNotificationID());

        updates.put(requesterRef, null);
        updates.put(bookRequestRef, null);
        updates.put(bookBorrowerIDRef, request.getRequesterID());
        updates.put(bookStatusRef, Book.Status.ACCEPTED.toString());
        updates.put(notificationsRef, acceptNotification);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRequestRef: " + bookRequestRef.toString());
                    Log.i(TAG, "requesterRef: " + requesterRef.toString());
                    Log.i(TAG, "bookBorrowerIDRef: " + bookBorrowerIDRef.toString());
                    Log.i(TAG, "bookStatusRef: " + bookStatusRef);
                    Log.i(TAG, "notificationsRef: " + notificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                    declineAllRequests(request.getBookID(), declineNotification);
                }
            }
        });
    }

    public static void declineAllRequests(final String bookID, final Notification notification) {
        DatabaseReference bookRequestRef = RequestCollectionRefUtils
                .getBookRequestCollectionRef(bookID);
        bookRequestRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String requesterID = dataSnapshot.getKey();
                Log.i(TAG, "requester being declined from book acceptance" + requesterID);
                notification.setNotificationReceiverID(requesterID);
                notification.setRequesterID(requesterID);
                declineRequest(requesterID, bookID, notification);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void declineRequest(String requesterID, String bookID, Notification notification) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String requesterRef = String.format("requestCollection/%s/%s",
                requesterID, bookID);
        final String bookRequestRef = String.format("bookRequests/%s/%s",
                bookID, requesterID);
        final String notificationsRef = String.format("notifications/%s/%s",
                notification.getNotificationReceiverID(),
                notification.getNotificationID());

        updates.put(requesterRef, null);
        updates.put(bookRequestRef, null);
        updates.put(notificationsRef, notification);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRequestRef: " + bookRequestRef.toString());
                    Log.i(TAG, "requesterRef: " + requesterRef.toString());
                    Log.i(TAG, "notificationsRef: " + notificationsRef.toString());
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    public static void deleteNotification(Notification notification) {
        deleteNotification(notification.getNotificationReceiverID(), notification.getNotificationID());
    }

    public static void deleteNotification(String notificationReceiverID, String notificationID) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String notificationsRef = String.format("notifications/%s/%s",
                notificationReceiverID, notificationID);

        updates.put(notificationsRef, null);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "notificationsRef: " + notificationsRef.toString());
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }
}