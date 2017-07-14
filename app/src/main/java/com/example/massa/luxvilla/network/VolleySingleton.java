package com.example.massa.luxvilla.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by massa on 22/01/2016.
 */
public class VolleySingleton {

    private static VolleySingleton mInstancia;
    private RequestQueue mrequestQueue;
    private ImageLoader mimageLoader;
    private static Context mContext;

    private VolleySingleton(Context context){
        mContext=context;
        mrequestQueue=getRequestQueue();

        mimageLoader=new ImageLoader(mrequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized VolleySingleton getInstancia(Context context){
        if(mInstancia==null){
            mInstancia=new VolleySingleton(context);
        }

        return mInstancia;
    }

    public RequestQueue getRequestQueue() {
        if (mrequestQueue == null) {

            mrequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mrequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mimageLoader;
    }


}
