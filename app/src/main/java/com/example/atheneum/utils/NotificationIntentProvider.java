package com.example.atheneum.utils;

import android.content.Context;
import android.content.Intent;

import com.example.atheneum.activities.BookInfoActivity;
import com.example.atheneum.activities.ShowRequestInfoActivity;
import com.example.atheneum.models.Book;
import com.example.atheneum.models.Notification;

public class NotificationIntentProvider {
    public static Intent obtainIntent(Context c, Notification notification) {
        Intent notifyIntent = null;
        if (notification.getrNotificationType() == Notification.NotificationType.REQUEST) {
            // this notification is received by owner
            notifyIntent = new Intent(c, BookInfoActivity.class);
            notifyIntent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.OWNER_VIEW);
        } else {
            // this notification is received by borrower
            notifyIntent = new Intent(c, ShowRequestInfoActivity.class);
            notifyIntent.putExtra(BookInfoActivity.VIEW_TYPE, BookInfoActivity.REQUSET_VIEW);
        }

        notifyIntent.putExtra(BookInfoActivity.BOOK_ID, notification.getBookID());
        // TODO: rStatus in ShowRequestInfoActivity should be obtained within itself
        // BELOW IS PLACEHOLDER AND SHOULD BE THE STATUS OF THE REQUEST INSTEAD OF THE NOTIFICATION
//        notifyIntent.putExtra(ShowRequestInfoActivity.RSTATUS, notification.getrNotificationType().toString());

        return notifyIntent;
    }
}
