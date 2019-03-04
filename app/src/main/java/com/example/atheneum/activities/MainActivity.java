package com.example.atheneum.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.fragments.AddBookFragment;
import com.example.atheneum.fragments.BorrowerPageFragment;
import com.example.atheneum.fragments.HomeFragment;
import com.example.atheneum.fragments.OwnerPageFragment;
import com.example.atheneum.fragments.SearchFragment;
import com.example.atheneum.fragments.ViewProfileFragment;
import com.example.atheneum.models.User;
import com.example.atheneum.utils.FirebaseAuthUtils;
import com.example.atheneum.utils.PhotoUtils;
import com.example.atheneum.viewmodels.UserViewModel;
import com.example.atheneum.viewmodels.UserViewModelFactory;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        final TextView nav_username = (TextView)((View) headerView).findViewById(R.id.nav_user_name);

        // Initially show the home fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).addToBackStack("Home").commit();

        // Update user information in the navbar
        if (FirebaseAuthUtils.isCurrentUserAuthenticated()) {
            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firebaseUser.getUid());
            UserViewModel userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel.class);
            LiveData<User> userLiveData = userViewModel.getUserLiveData();
            userLiveData.observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    Log.i(TAG, "in Observer!");
                    if (user != null) {
                        nav_username.setText(user.getUserName());

                        ArrayList<String> photos = user.getPhotos();
                        if (!photos.isEmpty()) {
                            Bitmap profilePic = PhotoUtils.DecodeBase64BitmapPhoto(photos.get(0));
                            ImageView imageView = (ImageView) ((View) navigationView).findViewById(R.id.nav_user_profile_picture);
                            imageView.setImageBitmap(profilePic);
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, "Shouldn't happen!");
        }
    }

    //See: https://stackoverflow.com/questions/7992216/android-fragment-handle-back-button-press
    //See: https://stackoverflow.com/questions/14460109/android-fragmenttransaction-addtobackstack-confusion
    //See: https://stackoverflow.com/questions/41431546/android-peek-backstack-without-popping
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        FragmentManager.BackStackEntry topBackStackEntry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
        String tag = topBackStackEntry.getName();

        Fragment homeFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("Home");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (tag == "Home") {
            Log.d(TAG, "Prevented removing home frag");
            return;
        } else if (count > 0) {
            Log.d(TAG, "Popped top fragment from stack");
            getSupportFragmentManager().popBackStack();
        } else {
            Log.d(TAG, "BACKPRESS");
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).addToBackStack("Home").commit();
        } else if (id == R.id.nav_profile) {

            FirebaseUser firebaseUser = FirebaseAuthUtils.getCurrentUser();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = db.getReference("users").child(firebaseUser.getUid());

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User thisUser = dataSnapshot.getValue(User.class);
                    getIntent().putExtra("user", thisUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewProfileFragment()).addToBackStack("ViewProfile").commit();

        } else if (id == R.id.nav_addbook)  {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AddBookFragment()).addToBackStack("AddBook").commit();
        } else if (id == R.id.nav_owner) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new OwnerPageFragment()).addToBackStack("OwnerPage").commit();
        } else if (id == R.id.nav_borrower) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new BorrowerPageFragment()).addToBackStack("BorrowerPage").commit();
        } else if (id == R.id.nav_search) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchFragment()).commit();

        } else if (id == R.id.nav_logout) {
            // Sign out of account and go back to authentication screen
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                            Intent intent = new Intent(getApplicationContext(), FirebaseUIAuthActivity.class);
                            startActivity(intent);
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // allows for setting title of action bar from different fragmenets
    // taken from https://stackoverflow.com/questions/15560904/setting-custom-actionbar-title-from-fragment
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Method for search user fragment to pass data to view profile fragment.
     * See: https://stackoverflow.com/questions/16036572/how-to-pass-values-between-fragments
     * See: https://stackoverflow.com/questions/12739909/send-data-from-activity-to-fragment-in-android
     */
    public void passDatatoFragment(User user) {
        getIntent().putExtra("user", user);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewProfileFragment()).commit();
    }

}
