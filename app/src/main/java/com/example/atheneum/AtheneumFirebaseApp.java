
package com.example.atheneum;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Have to extend the base Android application class in order to enable disk persistence
 *
 * See: https://stackoverflow.com/a/37766261/11039833
 */
public class AtheneumFirebaseApp extends android.app.Application {

        @Override
        public void onCreate() {
            super.onCreate();
            /* Enable disk persistence  */
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
}