package com.example.massa.luxvilla.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.massa.luxvilla.Actividades.casaactivity;
import com.example.massa.luxvilla.MainActivity;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.example.massa.luxvilla.utils.keys;
import com.example.massa.luxvilla.utils.listacasas;
import com.example.massa.luxvilla.utils.todascasas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by massa on 11/02/2016.
 */
public class notificationservice extends IntentService {
    VolleySingleton volleySingleton;
    static BDAdapter adapter;
    RequestQueue requestQueue;
    ImageLoader imageLoader;
    final String ISOPENAPP="appstate";
    SharedPreferences sharedPreferences;

    public notificationservice(){
        super("notificationservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        volleySingleton=VolleySingleton.getInstancia(this);
        requestQueue=volleySingleton.getRequestQueue();
        imageLoader=volleySingleton.getImageLoader();
        sharedPreferences=getSharedPreferences(ISOPENAPP,0);
        int flagopen=sharedPreferences.getInt("open",0);
            if (isNetworkAvailable(this) && flagopen==0)
                sendjsonRequest();


    }

    private void sendjsonRequest(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, "http://brunomassa.esy.es/resultado.json", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                parsejsonResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private ArrayList<todascasas> parsejsonResponse(JSONArray array){
        ArrayList<todascasas> casas=new ArrayList<>();
        int i;
        String id="";
        String local="";
        String preco="";
        String imgurl="";
        String info="";
        int newhouses=0;


        if (array!=null||array.length()>0){


            adapter=new BDAdapter(this);

            for (i=0;i<array.length();i++){
                try {
                    JSONObject casaexata=array.getJSONObject(i);
                    id=casaexata.getString(keys.allkeys.KEY_ID);
                    local=casaexata.getString(keys.allkeys.KEY_LOCAL);
                    preco=casaexata.getString(keys.allkeys.KEY_PRECO);
                    imgurl=casaexata.getString(keys.allkeys.KEY_IMGURL);
                    info=casaexata.getString(keys.allkeys.KEY_INFO);



                    String locsql=adapter.verlocais(id);
                    String precsql=adapter.verprecos(id);
                    String infossql=adapter.verinfos(id);
                    if (local.equalsIgnoreCase(locsql) && preco.equalsIgnoreCase(precsql) && info.equalsIgnoreCase(infossql)){
                        //Toast.makeText(this,"IGUAIS",Toast.LENGTH_LONG).show();
                    }else {
                        newhouses++;
                        /*long longid=adapter.inserirdados(local,preco,info);
                        if (longid>0){
                            Toast.makeText(this, "Casa adicionada ao modo offline", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(this, "Ocorreu um erro, tente novamente mais tarde", Toast.LENGTH_LONG).show();
                        }*/
                    }

                } catch (JSONException e) {


                }
            }

            if (newhouses>1){

                Bitmap imglarge;
                final NotificationCompat.Builder notification= new NotificationCompat.Builder(this)
                        .setTicker("Novas casas disponiveis")
                        .setSmallIcon(R.drawable.ic_account_balance_white_24dp)
                        .setContentTitle("Novas casas disponiveis")
                        .setContentText("Estão disponiveis novas casas")
                        .setAutoCancel(true);


                Intent it=new Intent(this, MainActivity.class);
                PendingIntent pi=PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);



                NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
                style.addLine("Estão disponiveis novas casas");
                style.setSummaryText("LuxVilla");


                notification.setContentIntent(pi);
                notification.setVibrate(new long[]{1000, 1000, 1000, 1000});
                notification.setNumber(newhouses);
                notification.setStyle(style);

                Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                notification.setSound(uri);

                notification.setPriority(NotificationCompat.PRIORITY_HIGH);
                imglarge= BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.logo);
                notification.setLargeIcon(imglarge);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(0, notification.build());

            }else {
                if (newhouses==1){

                    Bitmap imglarge;
                    final NotificationCompat.Builder notification= new NotificationCompat.Builder(this)
                            .setTicker("Nova casa disponivel")
                            .setSmallIcon(R.drawable.ic_account_balance_white_24dp)
                            .setContentTitle("Nova casa disponivel")
                            .setContentText("Esta disponivel uma nova casa")
                            .setAutoCancel(true);

                    TaskStackBuilder stack=TaskStackBuilder.create(this);
                    stack.addParentStack(casaactivity.class);

                    Intent it=new Intent(this, casaactivity.class);
                    it.putExtra("localcasa",local);
                    it.putExtra("precocasa",preco);
                    it.putExtra("infocs",info);
                    it.putExtra("imgurl", imgurl);
                    it.putExtra("csid", id);
                    stack.addNextIntent(it);

                    NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
                    style.addLine("Esta disponivel uma nova casa");
                    style.setSummaryText("LuxVilla");

                    PendingIntent pi=stack.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

                    notification.setContentIntent(pi);
                    notification.setVibrate(new long[]{1000, 1000, 1000, 1000});
                    notification.setStyle(style);

                    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    notification.setSound(uri);

                    notification.setPriority(NotificationCompat.PRIORITY_HIGH);
                    imglarge= BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.logo);
                    notification.setLargeIcon(imglarge);

                    imageLoader.get(imgurl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            notification.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response.getBitmap()));

                            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            nm.notify(0, notification.build());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                }

            }

        }


        return casas;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
