package com.example.atheneum.activities;

import android.support.constraint.ConstraintLayout;
import android.os.Bundle;

import com.example.atheneum.R;


/**
 * Default Activity after User sign's in.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remember this is the Constraint Layout area within content_base.xml
        ConstraintLayout contentFrameLayout = (ConstraintLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_main, contentFrameLayout);
        this.setTitle(R.string.title_activity_main);
    }
}
