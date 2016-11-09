package com.example.massa.luxvilla.adaptadores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.utils.sliderimgs;

import java.util.ArrayList;

/**
 * Created by massa on 08/11/2016.
 */

public class imageslideradapter extends PagerAdapter {

    private ArrayList<sliderimgs> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private ImageLoader imageLoader;

    public imageslideradapter(Context context,ArrayList<sliderimgs> images){
        this.context = context;
        this.IMAGES=images;
        inflater = LayoutInflater.from(context);
        VolleySingleton volleySingleton=VolleySingleton.getInstancia(context);
        imageLoader=volleySingleton.getImageLoader();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.imageslayout, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imgslider);

        sliderimgs slider=IMAGES.get(position);
        String url=slider.getIMGURL();
        if(url!=null){
            if (isNetworkAvailable(context)){
                imageLoader.get(url, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        imageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.drawable.logo);
                    }
                });
            }else {
                imageView.setImageResource(R.drawable.logo);
            }
        }
        view.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
