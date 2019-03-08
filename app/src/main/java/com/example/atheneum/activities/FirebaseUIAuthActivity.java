package com.example.atheneum.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.atheneum.R;
import com.example.atheneum.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

/**
 * Activity responsible for authentication (sign-in and sign-up) of a user.
 *
 * Leverages Firebase UI Auth library to dynamically generate the sign-in activity.
 * Entry point to the app.
 *
 */
public class FirebaseUIAuthActivity extends AppCompatActivity {
    final static String TAG = FirebaseUIAuthActivity.class.getSimpleName();
    static final int AUTH_RC_CODE = 1917;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui_auth);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            generateSignInPage();
        }
    }

    private void postSignUpTransition() {
        Intent intent = new Intent(getApplicationContext(), CompleteRegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     * See: https://www.androidauthority.com/android-push-notifications-with-firebase-cloud-messaging-925075/
     */
    private void postSignInTransition() {
        //store user's device token in datbase
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("userDeviceTokens")
            .child(firebaseUser.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Add user information to user table if the user doesn't exist in
                // our database
                User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                final DatabaseReference userTokensRef = db.getReference().child("userDeviceTokens")
                        .child(newUser.getUserID());

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.i(TAG, "cannot store user device token?!");
                                    return;
                                }

                                String token = task.getResult().getToken();
                                Log.d(TAG, token);

                                //Store user's instanceID token in firebase
                                userTokensRef.setValue(token);
                                Log.i(TAG, "User device token stored in database");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "user device token cannot be stored??");
            }
        });

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void generateSignInPage() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                                        .setIsSmartLockEnabled(false) // Disabled for testing, used for password-less login
                        .build(),
                AUTH_RC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTH_RC_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    final FirebaseDatabase db = FirebaseDatabase.getInstance();

                    DatabaseReference ref = db.getReference().child(getString(R.string.db_users)).child(firebaseUser.getUid());
                    // Check if the user's information exists in the database
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                // Add user information to user table if the user doesn't exist in
                                // our database
                                User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                                DatabaseReference userRef = db.getReference().child(getString(R.string.db_users)).child(newUser.getUserID());
                                userRef.setValue(newUser);
                                Log.i(TAG, "User doesn't exist in database!");
                                postSignUpTransition();
                            } else {
                                Log.i(TAG, "User already exists in database!");
                                postSignInTransition();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "User listener was cancelled");
                            postSignInTransition();
                        }
                    });

                } else {
                    // No user is signed in
                    Snackbar sb = Snackbar.make(findViewById(R.id.firebase_ui_auth_constraint_layout), R.string.no_user_signed_in, Snackbar.LENGTH_INDEFINITE);
                    sb.show();
                    Log.w(TAG, "If sign in successful, currentUser must be non-null! Strange condition!");
                    generateSignInPage();
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response == null) {
                    // User pressed back button
                    Snackbar sb = Snackbar.make(findViewById(R.id.firebase_ui_auth_constraint_layout), R.string.sign_in_cancelled, Snackbar.LENGTH_SHORT);
                    sb.show();
                    Log.w(TAG, "User pressed back button");
                    generateSignInPage();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar sb = Snackbar.make(findViewById(R.id.firebase_ui_auth_constraint_layout), R.string.no_internet_connection, Snackbar.LENGTH_SHORT);
                    sb.show();
                    Log.w(TAG, "No network");
                    // .info/connected is a special node that holds the connection status of a client. This
                    // node is not synchronized in the cloud since it is particular to each client.
                    // We add a listener to check for network connectivity events and if the network is connected
                    // we build the sign-in ui
                    final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                    connectedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            if (connected) {
                                generateSignInPage();
                                connectedRef.removeEventListener(this);
                            } else {
                                Snackbar sb = Snackbar.make(findViewById(R.id.firebase_ui_auth_constraint_layout), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
                                sb.show();
                                Log.d(TAG, "not connected");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w(TAG, "Connected listener was cancelled");
                        }
                    });
                } else {
                    Snackbar sb = Snackbar.make(findViewById(R.id.firebase_ui_auth_constraint_layout), R.string.unknown_error, Snackbar.LENGTH_SHORT);
                    sb.show();
                    Log.e(TAG, "Sign-in error: ", response.getError());
                    generateSignInPage();
                }
            }
        }
    }
}
