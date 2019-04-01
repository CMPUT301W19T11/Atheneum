package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;

/**
 * The type User device token ref utils.
 * utility class for getting firebase references to user tokens
 */
public class UserDeviceTokenRefUtils extends RootRefUtils {
    /**
     * The constant USER_DEVICE_TOKENS_REF.
     */
    public static final DatabaseReference USER_DEVICE_TOKENS_REF = ROOT_REF.child("userDeviceTokens");

    /**
     * Gets user device tokens ref.
     *
     * @param userID the user id
     * @return the user device tokens ref
     */
    public static final DatabaseReference getUserDeviceTokensRef(String userID) {
        return USER_DEVICE_TOKENS_REF.child(userID);
    }
}
