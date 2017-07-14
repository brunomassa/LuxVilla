package com.example.massa.luxvilla.utils;

/**
 * Created by massa on 06/07/2017.
 */

public class mailcheck {

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
