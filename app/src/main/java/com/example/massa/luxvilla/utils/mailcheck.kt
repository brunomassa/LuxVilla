package com.example.massa.luxvilla.utils

/**
 * Created by massa on 27/10/2017.
 */
object mailcheck {

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}