package com.example.massa.luxvilla.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
    final String ISOPENAPP="appstate";
    SharedPreferences sharedPreferences;

    public notificationservice(){
        super("notificationservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        volleySingleton=VolleySingleton.getInstancia(this);
        requestQueue=volleySingleton.getRequestQueue();
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


        if (array!=null||array.length()>0){

            adapter=new BDAdapter(this);

            for (int i=0;i<array.length();i++){
                try {
                    JSONObject casaexata=array.getJSONObject(i);
                    String id=casaexata.getString(keys.allkeys.KEY_ID);
                    String local=casaexata.getString(keys.allkeys.KEY_LOCAL);
                    String preco=casaexata.getString(keys.allkeys.KEY_PRECO);
                    String imgurl=casaexata.getString(keys.allkeys.KEY_IMGURL);
                    String info=casaexata.getString(keys.allkeys.KEY_INFO);



                    String locsql=adapter.verlocais(id);
                    String precsql=adapter.verprecos(id);
                    String infossql=adapter.verinfos(id);
                    if (local.equalsIgnoreCase(locsql) && preco.equalsIgnoreCase(precsql) && info.equalsIgnoreCase(infossql)){
                        Toast.makeText(this,"IGUAIS",Toast.LENGTH_LONG).show();
                    }else {
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

        }


        return casas;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
