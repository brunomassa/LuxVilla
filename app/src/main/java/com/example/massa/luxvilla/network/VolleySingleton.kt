package com.example.massa.luxvilla.network

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by massa on 27/10/2017.
 */
class VolleySingleton private constructor(context: Context) {
    private var mrequestQueue: RequestQueue? = null
    val imageLoader: ImageLoader
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

        imageLoader = ImageLoader(mrequestQueue, object : ImageLoader.ImageCache {
            val cache = LruCache<String, Bitmap>(20)

            override fun getBitmap(url: String): Bitmap {
                return cache.get(url)
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }
        })
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
