package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RootRefUtils {
    public static final DatabaseReference ROOT_REF = FirebaseDatabase.getInstance().getReference();
}
