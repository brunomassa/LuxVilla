package com.example.massa.luxvilla.Actividades;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.network.VolleySingleton;

public class casaactivity extends AppCompatActivity {

    String localcasa;
    String precocasa;
    String imgurlcasa;
    String infocasa;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    Toolbar toolbarinfocasa;
    ImageView imageViewinfocasa;
    TextView textViewinfocasa;
    ViewGroup mRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            getWindow().setSharedElementEnterTransition(new ChangeBounds());

            Transition transitioncardview=getWindow().getSharedElementEnterTransition();
            transitioncardview.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    TransitionManager.beginDelayedTransition(mRoot,new Slide());
                    mRoot.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casaactivity);

        localcasa=getIntent().getStringExtra("localcasa");
        precocasa=getIntent().getStringExtra("precocasa");
        imgurlcasa=getIntent().getStringExtra("imgurl");
        infocasa=getIntent().getStringExtra("infocs");

        toolbarinfocasa=(Toolbar)findViewById(R.id.barinfocasaactivity);
        toolbarinfocasa.setTitle(localcasa);
        toolbarinfocasa.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbarinfocasa.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageViewinfocasa=(ImageView)findViewById(R.id.imginfocasaactivity);
        textViewinfocasa=(TextView)findViewById(R.id.txtinfocasaactivity);

        volleySingleton=VolleySingleton.getInstancia(casaactivity.this);
        requestQueue=volleySingleton.getRequestQueue();
        imageLoader=volleySingleton.getImageLoader();

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
        }



        textViewinfocasa.setText("Preço: " + precocasa+"\n\n"+infocasa);

        mRoot=(ViewGroup)findViewById(R.id.card_viewinfo);


        //Toast.makeText(casaactivity.this,localcasa+" "+precocasa+" "+imgurlcasa,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            TransitionManager.beginDelayedTransition(mRoot,new Slide());
            mRoot.setVisibility(View.INVISIBLE);
        }
        super.onBackPressed();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
