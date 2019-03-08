package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;

public class UserDeviceTokenRefUtils extends RootRefUtils {
    public static final DatabaseReference USER_DEVICE_TOKENS_REF = ROOT_REF.child("userDeviceTokens");

    public static final DatabaseReference getUserDeviceTokensRef(String userID) {
        return USER_DEVICE_TOKENS_REF.child(userID);
    }
}
