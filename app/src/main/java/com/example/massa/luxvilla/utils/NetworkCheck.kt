package com.example.massa.luxvilla.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by massa on 27/10/2017.
 */
object NetworkCheck {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}