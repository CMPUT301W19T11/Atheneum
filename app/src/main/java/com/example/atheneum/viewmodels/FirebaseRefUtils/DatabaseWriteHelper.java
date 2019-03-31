package com.example.atheneum.viewmodels.FirebaseRefUtils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Notification;
import com.example.atheneum.models.Photo;
import com.example.atheneum.models.Request;
import com.example.atheneum.models.Transaction;
import com.example.atheneum.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class to abstract writes to the database. Abstracting writes to the database in a central
 * hides information about the database from the views and makes it easier to write multi-path updates
 * correctly since multi-path updates can touch multiple paths within the database.
 *
 * This class should be updated whenever new actions on the database are added.
 */
public class DatabaseWriteHelper {
    private static final String TAG = DatabaseWriteHelper.class.getSimpleName();

    /**
     * Add a new book to the Database
     *
     * @param owner Owner of the book.
     * @param newBook Book object to add to Database.
     */
    public static void addNewBook(User owner, Book newBook) {
        HashMap<String, Object> updates = new HashMap<String, Object>();
        // Atomically update multiple database paths at once by using a multi-path update
        // Note: Can't use the DatabaseReference objects and then convert to a key, must use formatted
        // strings when doing multi-path updates
        final String bookRef = String.format("books/%s", newBook.getBookID());
        final String ownerBookRef = String.format("ownerCollection/%s/%s", owner.getUserID(), newBook.getBookID());
        updates.put(bookRef, newBook);
        // Even though we are just interested in the bookID, we have to store a value for the bookID
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

    /**
     * Removes a book and it's associated data from the Database.
     *
     * @param ownerUserID UserID of the book's owner.
     * @param bookID Unique identifier of the book.
     */
    public static void deleteBook(String ownerUserID, String bookID) {
        HashMap<String, Object> updates = new HashMap<String, Object>();
        // Atomically update multiple database paths at once by using a multi-path update
        // Note: Can't use the DatabaseReference objects and then convert to a key, must use formatted
        // strings when doing multi-path updates
        final String bookRef = String.format("books/%s", bookID);
        final String ownerBookRef = String.format("ownerCollection/%s/%s", ownerUserID, bookID);
        final String bookPhotoRef = String.format("bookPhotos/%s", bookID);
        // Add null to a node to delete it
        updates.put(bookRef, null);
        updates.put(ownerBookRef, null);
        updates.put(bookPhotoRef, null);
        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRef: " + bookRef.toString());
                    Log.i(TAG, "ownerBookRef: " + ownerBookRef.toString());
                    Log.i(TAG, "bookPhotoRef: " + bookPhotoRef.toString());
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    /**
     * Removes a book and it's associated data from the Database.
     *
     * @param owner Owner of the book.
     * @param book Book object to remove from the Database.
     */
    public static void deleteBook(User owner, Book book) {
        deleteBook(owner.getUserID(), book.getBookID());
    }

    /**
     * Updates a book in the Database.
     *
     * @param book New value of Book.
     */
    public static void updateBook(Book book) {
        BooksRefUtils.getBookRef(book).setValue(book);
    }

    /**
     * Add an new request to the Database.
     *
     * @param request Request to add.
     * @param notification Notification detailing request.
     */
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
        final String pushNotificationsRef = String.format("pushNotifications/%s/%s",
                notification.getNotificationReceiverID(),
                notification.getNotificationID());
        final String bookStatusRef = String.format("books/%s/status", request.getBookID());

        updates.put(requesterRef, request);
        updates.put(bookRequestRef, true);
        updates.put(notificationsRef, notification);
        updates.put(pushNotificationsRef, notification);
        updates.put(bookStatusRef, Book.Status.REQUESTED.toString());
        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRequestRef: " + bookRequestRef.toString());
                    Log.i(TAG, "requesterRef: " + requesterRef.toString());
                    Log.i(TAG, "notificationRef: " + notificationsRef.toString());
                    Log.i(TAG, "pushNotificationsRef: " + pushNotificationsRef);
                    Log.i(TAG, "bookStatusRef: " + bookStatusRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    /**
     * Accept a request on a book.
     *
     * @param request Request to accept.
     * @param acceptNotification Notification sent to book's accepted requester.
     * @param declineNotification Notification sent to users whose requests have been declined.
     */
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
        final String pushNotificationsRef = String.format("pushNotifications/%s/%s",
                acceptNotification.getNotificationReceiverID(),
                acceptNotification.getNotificationID());

        updates.put(requesterRef, request);
        updates.put(bookRequestRef, null);
        updates.put(bookBorrowerIDRef, request.getRequesterID());
        updates.put(bookStatusRef, Book.Status.ACCEPTED.toString());
        updates.put(notificationsRef, acceptNotification);
        updates.put(pushNotificationsRef, acceptNotification);

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
                    Log.i(TAG, "pushNotificationsRef: " + pushNotificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                    declineAllRequests(request.getBookID(), declineNotification);
                }
            }
        });
    }


    /**
     * Triggers when a request is accepted
     * Used only by other DatabaseWriteHelper methods
     *
     * @param bookID
     * @param notification
     */
    public static void declineAllRequests(final String bookID, final Notification notification) {
        DatabaseReference bookRequestRef = RequestCollectionRefUtils
                .getBookRequestCollectionRef(bookID);
        bookRequestRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String requesterID = dataSnapshot.getKey();
                Log.i(TAG, "requester being declined from book acceptance: " + requesterID);
                notification.setNotificationReceiverID(requesterID);
                notification.setRequesterID(requesterID);
                Log.i(TAG, "notification requester ID: " + notification.getRequesterID());
                declineRequest(requesterID, bookID, notification, false);
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

    public static void declineRequest(String requesterID, final String bookID,
                                      Notification notification, final boolean shouldUpdateStatus) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String requesterRef = String.format("requestCollection/%s/%s/rStatus",
                requesterID, bookID);
        final String bookRequestRef = String.format("bookRequests/%s/%s",
                bookID, requesterID);
        final String notificationsRef = String.format("notifications/%s/%s",
                notification.getNotificationReceiverID(),
                notification.getNotificationID());
        final String pushNotificationsRef = String.format("pushNotifications/%s/%s",
                notification.getNotificationReceiverID(),
                notification.getNotificationID());

        updates.put(requesterRef, Request.Status.DECLINED);
        updates.put(bookRequestRef, null);
        updates.put(notificationsRef, notification);
        updates.put(pushNotificationsRef, notification);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "bookRequestRef: " + bookRequestRef.toString());
                    Log.i(TAG, "requesterRef: " + requesterRef.toString());
                    Log.i(TAG, "notificationsRef: " + notificationsRef.toString());
                    Log.i(TAG, "pushNotificationsRef: " + pushNotificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                    if (shouldUpdateStatus) updateBookStatusToAvailable(bookID);
                }
            }
        });
    }

    /**
     * Triggers when all requests on a book are declined (no requests are accepted)
     * Used only by other DatabaseWriteHelper methods
     */
    public static void updateBookStatusToAvailable(final String bookID) {
        DatabaseReference bookRequestRef = RequestCollectionRefUtils
                .getBookRequestCollectionRef(bookID);
        bookRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "number of requesters: " + Long.toString(dataSnapshot.getChildrenCount()));
                // only update status to AVAILABLE when there are no more requesters
                if (dataSnapshot.getChildrenCount() == 0) {
                    HashMap<String, Object> updates = new HashMap<String, Object>();
                    final String bookStatusRef = String.format("books/%s/status", bookID);
                    updates.put(bookStatusRef, Book.Status.AVAILABLE.toString());

                    RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.w(TAG, "Error updating data at " + databaseReference.toString());
                                Log.i(TAG, "bookRequestRef: " + bookStatusRef);
                            } else {
                                Log.i(TAG, "Successful update at " + databaseReference.toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void deletePushNotification(Notification notification) {
        deletePushNotification(notification.getNotificationReceiverID(), notification.getNotificationID());
    }

    public static void deletePushNotification(String notificationReceiverID, String notificationID) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String pushNotificationsRef = String.format("pushNotifications/%s/%s",
                notificationReceiverID, notificationID);

        updates.put(pushNotificationsRef, null);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "pushNotificationsRef: " + pushNotificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    public static void deleteNotification(Notification notification) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String notificationsRef = String.format("notifications/%s/%s",
                notification.getNotificationReceiverID(), notification.getNotificationID());

        updates.put(notificationsRef, null);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "notificationsRef: " + notificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    public static void makeNotificationSeen(Notification notification) {
        HashMap<String, Object> updates = new HashMap<String, Object>();

        final String notificationsRef = String.format("notifications/%s/%s/isSeen",
                notification.getNotificationReceiverID(), notification.getNotificationID());

        updates.put(notificationsRef, true);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.w(TAG, "Error updating data at " + databaseReference.toString());
                    Log.i(TAG, "notificationsRef: " + notificationsRef);
                } else {
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }


    public static void addNewTransaction(Transaction transaction){
        TransactionRefUtils.TRANSACTION_REF.child(transaction.getBookID()).setValue(transaction);

    }

    public static void updateTransaction(Transaction transaction){
        TransactionRefUtils.getTransactionRef(transaction.getBookID()).setValue(transaction);
    }

    public static void updateTransactionBookBorrow(Book book, Transaction transaction){
        HashMap<String, Object> updates = new HashMap<>();

        final String transactionTypeRef = String.format("transactions/%s/type",
                book.getBookID());

        final String transactionOScanRef = String.format("transactions/%s/oScan",
                book.getBookID());

        final String transactionBScanRef = String.format("transactions/%s/bScan",
                book.getBookID());

        final String bookStatusRef = String.format("books/%s/status",
                book.getBookID());

        final String requestRef = String.format("requestCollection/%s/%s",
                transaction.getBorrowerID(), transaction.getBookID());

        final String borrowedBooksHistoryIsbnRef = String.format("borrowedBooksHistory/%s/%s",
                transaction.getBorrowerID(), book.getIsbn());

        updates.put(transactionTypeRef, Transaction.RETURN);
        updates.put(transactionOScanRef, false);
        updates.put(transactionBScanRef, false);
        updates.put(bookStatusRef, Book.Status.BORROWED);
        updates.put(borrowedBooksHistoryIsbnRef, true);
//        updates.put(requestRef, null);

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.w(TAG, "Error updating data at" + databaseReference.toString());
                }
                else{
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                    Log.i(TAG, "transaction type: " + transactionTypeRef);
                    Log.i(TAG, "oscan type: "  + transactionOScanRef);
                    Log.i(TAG, "bscantype: " + transactionBScanRef);
                    Log.i(TAG, "book Status: " + bookStatusRef);
                    Log.i(TAG, "request:" + requestRef);
                }
            }
        });
    }

    public static void updateTransactionBookReturn(Book book){
        HashMap<String, Object> updates = new HashMap<>();

        final String transactionTypeRef = String.format("transactions/%s",
                book.getBookID());

        final String bookStatusRef = String.format("books/%s/status",
                book.getBookID());

        final String bookBorrowerIDRef = String.format("books/%s/borrowerID",
                book.getBookID());

        updates.put(transactionTypeRef, null);
        updates.put(bookStatusRef, Book.Status.AVAILABLE);
        updates.put(bookBorrowerIDRef, "");

        RootRefUtils.ROOT_REF.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.w(TAG, "Error updating data at" + databaseReference.toString());
                }
                else{
                    Log.i(TAG, "Successful update at " + databaseReference.toString());
                }
            }
        });
    }

    /**
     * Add a photo for a particular book to the Database.
     *
     * @param bookID Unique identifier for the book that the photo belongs to.
     * @param newBitmap New picture taken from camera.
     */
    public static void addBookPhoto(String bookID, Bitmap newBitmap) {
        DatabaseReference bookPhotoRef = BookPhotosRefUtils.getBookPhotosRef(bookID).push();
        bookPhotoRef.setValue(new Photo(bookPhotoRef.getKey(), newBitmap));
    }

    /**
     * Update a photo for a book.
     *
     * @param bookID Unique identifier for the book that the photo belongs to.
     * @param photo Photo object representing current state of the photo in the Database.
     * @param newBitmap New picture taken from camera.
     */
    public static void updateBookPhoto(String bookID, Photo photo, Bitmap newBitmap) {
        photo.setEncodedString(Photo.EncodeBitmapPhotoBase64(newBitmap));
        BookPhotosRefUtils.getBookPhotoRef(bookID, photo.getPhotoID()).setValue(photo);
    }

    /**
     * Delete a photo associated with a book.
     *
     * @param bookID Unique identifier for the book that the photo belongs to.
     * @param photo Photo object.
     */
    public static void deleteBookPhoto(String bookID, Photo photo) {
        BookPhotosRefUtils.getBookPhotoRef(bookID, photo.getPhotoID()).removeValue();

    }
}