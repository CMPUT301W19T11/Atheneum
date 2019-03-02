package com.example.atheneum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.fragments.AddBookFragment;
import com.example.atheneum.fragments.BorrowerPageFragment;
import com.example.atheneum.fragments.HomeFragment;
import com.example.atheneum.fragments.OwnerPageFragment;
import com.example.atheneum.fragments.SearchFragment;
import com.example.atheneum.fragments.ViewProfileFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// See: https://stackoverflow.com/a/36103112/11039833
// See: https://stackoverflow.com/a/19451842/11039833
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView nav_username = (TextView)((View) headerView).findViewById(R.id.nav_user_name);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Set the user's email on the nav bar
            nav_username.setText(firebaseUser.getEmail());
        } else {
            Log.w(TAG, "User should be authenticated if the user is in this activity!");
        }

        // Initially show the home fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewProfileFragment()).commit();
        } else if (id == R.id.nav_addbook)  {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AddBookFragment()).commit();
        } else if (id == R.id.nav_owner) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new OwnerPageFragment()).commit();
        } else if (id == R.id.nav_borrower) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new BorrowerPageFragment()).commit();
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
}
