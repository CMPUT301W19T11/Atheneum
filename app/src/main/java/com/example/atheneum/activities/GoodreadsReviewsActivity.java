package com.example.atheneum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.atheneum.R;

/**
 * An activity to hold and show the reviews page of the book from goodreads. This was done since
 * Goodreads removed having reviews directly available in their API in 2012.
 */
public class GoodreadsReviewsActivity extends AppCompatActivity {
    public static final String WEBVIEW_URL = "webview_url";
    private String urlToShow;
    private WebView webView;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_widget);

        // get HTML string from intent
        Intent intent = getIntent();
        urlToShow = intent.getStringExtra(WEBVIEW_URL);
        webView = findViewById(R.id.reviewsWebView);

        if (urlToShow != null) {
            webView.loadUrl(urlToShow);
        }
        else {
            // TODO display error text
        }


    }
}

