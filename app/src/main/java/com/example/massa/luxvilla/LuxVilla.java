package com.example.massa.luxvilla;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by massa on 05/12/2016.
 */

public class LuxVilla extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
