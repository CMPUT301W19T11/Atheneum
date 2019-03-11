package com.example.atheneum.activities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignInTest.class,
        ViewEditProfileActivityTest.class,
        AddBookTest.class,
        ViewEditBookTest.class,
        DeleteBookTest.class,
        SignOutTest.class,
})
/**
 * Class that runs all the UI tests in the proper order.
 *
 * Add any classes that test main functionality between the SignIn and SignOut classes inside of the
 * annotation.
 */
public class AtheneumTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
