package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * The type Users ref utils.
 * utility class for getting firebase references to users
 */
public class UsersRefUtils extends RootRefUtils {
    /**
     * The constant USERS_REF.
     */
    public static final DatabaseReference USERS_REF = ROOT_REF.child("users");

    /**
     * Gets users ref.
     *
     * @param user the user
     * @return the users ref
     */
    public static final DatabaseReference getUsersRef(User user) {
        return USERS_REF.child(user.getUserID());
    }

    /**
     * Gets users ref.
     *
     * @param userID the user id
     * @return the users ref
     */
    public static final DatabaseReference getUsersRef(String userID) {
        return USERS_REF.child(userID);
    }

    /**
     * Gets user name query.
     *
     * @param partialUserName the partial user name
     * @return the user name query
     */
    public static final Query getUserNameQuery(String partialUserName) {
        return USERS_REF.orderByChild("userName").startAt(partialUserName).endAt(partialUserName + "\uf8ff");
    }
}
