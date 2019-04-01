package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The type Root ref utils.
 * utility class for getting firebase references to root
 */
public class RootRefUtils {
    /**
     * The constant ROOT_REF.
     */
    public static final DatabaseReference ROOT_REF = FirebaseDatabase.getInstance().getReference();
}
