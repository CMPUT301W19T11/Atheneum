package com.example.atheneum.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Utility class to reduce boilerplate code for dealing with FirebaseAuth
 */
public class FirebaseAuthUtils {
    private static final FirebaseAuth authInstance = FirebaseAuth.getInstance();

    /**
     * Gets current user.
     *
     * @return Current user
     */
    public static FirebaseUser getCurrentUser() {
        return authInstance.getCurrentUser();
    }

    /**
     * Returns true if user is authenticated, false otherwise
     *
     * @param user User to check authentication
     * @return Authentication status of user
     */
    public static boolean isUserAuthenticated(FirebaseUser user) {
        return user != null;
    }

    /**
     * Returns true if current user is authenticated, false otherwise
     *
     * @return Authentication status of current user
     */
    public static boolean isCurrentUserAuthenticated() {
        return isUserAuthenticated(getCurrentUser());
    }
}
