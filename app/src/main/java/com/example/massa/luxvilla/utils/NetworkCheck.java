package com.example.massa.luxvilla.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by massa on 30/03/2017.
 */

public class NetworkCheck {
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return (connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null) != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
