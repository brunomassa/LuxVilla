package com.example.massa.luxvilla.Application

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by massa on 27/10/2017.
 */
class LuxVilla : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}
