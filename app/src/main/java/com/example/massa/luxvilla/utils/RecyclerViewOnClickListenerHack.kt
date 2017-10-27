package com.example.massa.luxvilla.utils

import android.view.View

/**
 * Created by massa on 27/10/2017.
 */
interface RecyclerViewOnClickListenerHack {
    fun onClickListener(view: View, position: Int)
    fun onLongPressClickListener(view: View, position: Int)
}