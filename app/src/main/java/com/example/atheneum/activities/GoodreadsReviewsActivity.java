package com.example.atheneum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.atheneum.R;

/**
 * An activity to hold and show the reviews page of the book from goodreads. This was done since
 * Goodreads removed having reviews directly available in their API in 2012. The URL was  taken from
 * the API response of the goodreads request
 */
public class GoodreadsReviewsActivity extends AppCompatActivity {
    /**
     * The constant WEBVIEW_URL.
     */
    public static final String WEBVIEW_URL = "webview_url";
    private static final String TAG = "GRReviewsActivity";

    private String urlToShow;
    private WebView webView;
    private TextView errorMessageTextView;

    // used within the webviewclient
    private GoodreadsReviewsActivity goodreadsReviewsActivity = this;

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
        errorMessageTextView = findViewById(R.id.webviewErrorTextview);
        webView = findViewById(R.id.reviewsWebView);
        webView.setWebViewClient(new WebViewClient() {
            // to deal with errors
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.e(GoodreadsReviewsActivity.TAG, "Error loading page");

                goodreadsReviewsActivity.showWebviewErrorMessage("Invalid page! \nReviews are not available for this book!");
            }

            // deal with 404 errors
            // Taken from https://stackoverflow.com/questions/3181843/how-can-i-check-from-android-webview-if-a-page-is-a-404-page-not-found
            // License: https://creativecommons.org/licenses/by-sa/3.0/
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String pageTitle = webView.getTitle();
                Log.i(TAG, "Title:" + pageTitle);
                String[] separated = pageTitle.split("-");
                if(separated[0].equals("404")) {
                    Log.i(TAG, "detect page not found error 404");
                    goodreadsReviewsActivity.showWebviewErrorMessage("Invalid page! \nReviews are not available for this book!");
                }
                else if (pageTitle.equals("about:blank")){
                    Log.i(TAG, "detect blank page");
                    goodreadsReviewsActivity.showWebviewErrorMessage("Reviews are not available for this book!");
                }
                else {
                    goodreadsReviewsActivity.showWebview();
                }
            }
        });

        if (urlToShow != null) {
            Log.i(TAG, "URL : " + urlToShow);
            webView.loadUrl(urlToShow);
        }
        else { // URL was none
            showWebviewErrorMessage("No Reviews Available");
        }
    }

    /**
     * hide the webview and show an error message in its place
     * @param message the message to show
     */
    private void showWebviewErrorMessage(String message) {
        webView.setVisibility(View.GONE);
        errorMessageTextView.setText(message);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the error message and show the webview
     */
    private void showWebview() {
        errorMessageTextView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }
}

