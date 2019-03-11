package com.example.atheneum.models;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton for app wide RequestQueue for API calls
 *
 * Taken from https://developer.android.com/training/volley/requestqueue
 *
 */
public class SingletonRequestQueue {
    private static SingletonRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context context;

    /**
     * Get the instance of the singleton
     *
     * @param context context where the singleton is invoked. Realistically only needed once to get the application context
     * @return the existing instance of SingletoneRequestQueue, or a new one if none existed
     */
    public static synchronized SingletonRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonRequestQueue(context);
        }
        return instance;
    }

    /**
     * Constructor for the SingletonRequestQueue
     * context is needed to retrieve the ApplicationContext for Volley.
     * @param context the calling context
     */
    private SingletonRequestQueue(Context context) {
        this.context = context;

        requestQueue = getRequestQueue();
    }

    /**
     * Initialize a singleton object of RequestQueue type within the singleton. This is for allowing
     * expantion of the class to potentially hold other data as well. Currently, this is not necessarily
     * needed and could be included in the SingletonRequestQueue constructor.
     *
     * Used when accessing the request queue.
     *
     * @return the RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Add a request to the requestQueue
     *
     * @param req the request object
     * @param <T> the generic type parameter for the request object
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
