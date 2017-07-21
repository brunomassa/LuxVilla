package com.example.massa.luxvilla.Actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;

public class casaactivity extends AppCompatActivity {

    String localcasa;
    String precocasa;
    String imgurlcasa;
    String infocasa;
    String idcasa;
    Toolbar toolbarinfocasa;
    ImageView imageViewinfocasa;
    TextView textViewinfocasa;
    ViewGroup mRoot;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout imgtoolbar;
    LikeButton favoriteButton;
    SharedPreferences sharedPreferences;
    SharedPreferences isappopen;
    SharedPreferences.Editor editor;
    String PREFSNAME = "FAVS";
    final String ISOPENAPP="appstate";
    int favflag;
    int nightModeFlags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            Transition tr=new ChangeBounds();
            getWindow().setSharedElementEnterTransition(tr);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casaactivity);

        nightModeFlags =
                casaactivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        isappopen=getSharedPreferences(ISOPENAPP, 0);
        editor=isappopen.edit();
        editor.putInt("open", 1);
        editor.apply();

        localcasa=getIntent().getStringExtra("localcasa");
        precocasa=getIntent().getStringExtra("precocasa");
        imgurlcasa=getIntent().getStringExtra("imgurl");
        infocasa=getIntent().getStringExtra("infocs");
        idcasa=getIntent().getStringExtra("csid");

        appBarLayout=(AppBarLayout)findViewById(R.id.infocasaappbar);
        toolbarinfocasa=(Toolbar)findViewById(R.id.barinfocasaactivity);
        toolbarinfocasa.setTitle(localcasa);
        if (isNetworkAvailable(casaactivity.this)){
            toolbarinfocasa.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        }else {
            toolbarinfocasa.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        }

        imageViewinfocasa=(ImageView) findViewById(R.id.imginfocasaactivity);
        toolbarinfocasa.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgtoolbar=(CollapsingToolbarLayout)findViewById(R.id.tbinfocasaactivity);


        textViewinfocasa=(TextView)findViewById(R.id.txtinfocasaactivity);

        VolleySingleton volleySingleton = VolleySingleton.getInstancia(casaactivity.this);
        RequestQueue requestQueue = volleySingleton.getRequestQueue();
        ImageLoader imageLoader = volleySingleton.getImageLoader();

        if (isNetworkAvailable(casaactivity.this)) {

            imageLoader.get(imgurlcasa, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    imageViewinfocasa.setImageBitmap(response.getBitmap());
                }
                @Override
                public void onErrorResponse(VolleyError error) {

                }

            });

        }else {
            imageViewinfocasa.setImageResource(R.drawable.logo);
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    imgtoolbar.setExpandedTitleColor(ContextCompat.getColor(casaactivity.this,android.R.color.black));
                    imgtoolbar.setCollapsedTitleTextColor(ContextCompat.getColor(casaactivity.this,android.R.color.white));
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    imgtoolbar.setExpandedTitleColor(ContextCompat.getColor(casaactivity.this,android.R.color.black));
                    imgtoolbar.setCollapsedTitleTextColor(ContextCompat.getColor(casaactivity.this,android.R.color.black));
                    break;
            }
        }


        favoriteButton=(LikeButton) findViewById(R.id.favbuttoncasa);
        favoriteButton.setIcon(IconType.Heart);
        favoriteButton.setIconSizeDp(25);
        favoriteButton.setCircleEndColorRes(R.color.colorAccent);
        favoriteButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent);
        favoriteButton.setLikeDrawableRes(R.drawable.heartliked);
        favoriteButton.setUnlikeDrawableRes(R.drawable.heartunliked);

        sharedPreferences=getSharedPreferences(PREFSNAME, 0);
        favflag=sharedPreferences.getInt(idcasa, 0);

        firebaseutils.checklike(casaactivity.this,idcasa,favoriteButton);


        favoriteButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                favflag = sharedPreferences.getInt(idcasa, 0);

                sharedPreferences = getSharedPreferences(PREFSNAME, 0);
                editor = sharedPreferences.edit();
                editor.putInt(idcasa, 1);
                editor.apply();
                favflag = sharedPreferences.getInt(idcasa, 0);

                firebaseutils.setlike(idcasa);

                favoriteButton.setLiked(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {

                favflag = sharedPreferences.getInt(idcasa, 0);

                sharedPreferences = getSharedPreferences(PREFSNAME, 0);
                editor = sharedPreferences.edit();
                editor.putInt(String.valueOf(idcasa), 0);
                editor.apply();
                favflag = sharedPreferences.getInt(String.valueOf(idcasa), 0);

                firebaseutils.removelike(idcasa);

                favoriteButton.setLiked(false);
            }
        });


        textViewinfocasa.setText("Preço: " + precocasa+"\n\n"+infocasa);

        mRoot=(ViewGroup)findViewById(R.id.card_viewinfo);

    }

    @Override
    public void onBackPressed() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            TransitionManager.beginDelayedTransition(mRoot,new Slide());
            mRoot.setVisibility(View.INVISIBLE);
        }
        if (isTaskRoot()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            super.onBackPressed();
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
