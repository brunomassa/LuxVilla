package com.example.massa.luxvilla.network

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by massa on 03/11/2017.
 */
class VolleySingleton private constructor(context: Context) {
    private var mrequestQueue: RequestQueue? = null
    var mContext: Context? = null


    val requestQueue: RequestQueue
        get() {
            if (mrequestQueue == null) {
                mrequestQueue = Volley.newRequestQueue(mContext?.applicationContext)
            }
            return this.mrequestQueue!!
        }

    init {
        mContext = context
        mrequestQueue = requestQueue

    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstancia: VolleySingleton? = null
        @Synchronized
        fun getInstancia(context: Context): VolleySingleton {

            if (mInstancia == null) {
                mInstancia = VolleySingleton(context)

            }
            return mInstancia as VolleySingleton
        }
    }
}