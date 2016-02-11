package com.example.massa.luxvilla;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.example.massa.luxvilla.separadores.separadoraveiro;
import com.example.massa.luxvilla.separadores.separadorbraga;
import com.example.massa.luxvilla.separadores.separadorporto;
import com.example.massa.luxvilla.separadores.separadortodas;
import com.example.massa.luxvilla.services.notificationreciver;
import com.example.massa.luxvilla.services.notificationservice;
import com.example.massa.luxvilla.sqlite.BDAdapter;

import java.util.Calendar;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements MaterialTabListener {

    MaterialTabHost tabs;
    ViewPager vwpgr;
    public Toolbar barracima;
    final int SEPARADOR_TODAS=0;
    final int SEPARADOR_AVEIRO=1;
    final int SEPARADOR_BRAGA=2;
    final int SEPARADOR_PORTO=3;
    SearchView searchView;
    BDAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            getWindow().setSharedElementExitTransition(new ChangeBounds());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barracima=(Toolbar)findViewById(R.id.brcima);
        barracima.setTitle("LuxVilla: Todas");
        barracima.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(barracima);
        tabs= (MaterialTabHost) findViewById(R.id.materialTabHost);
        tabs.addTab(tabs.newTab().setText("TODAS").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("AVEIRO").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("BRAGA").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("PORTO").setTabListener(this));
        vwpgr= (ViewPager) findViewById(R.id.vpgr);
        adapter=new BDAdapter(this);
        int numerocolunas=adapter.numerodecolunas();
        if (numerocolunas==0 && !isNetworkAvailable(this)){
            vwpgr.setVisibility(View.GONE);
            tabs.setVisibility(View.GONE);
        }else {
            adaptadorpaginas adaptador=new adaptadorpaginas(getSupportFragmentManager());
            vwpgr.setAdapter(adaptador);

            vwpgr.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    tabs.setSelectedNavigationItem(position);
                    switch (position){
                        case 0:
                            barracima.setTitle("LuxVilla: Todas");
                            break;
                        case 1:
                            barracima.setTitle("LuxVilla: Aveiro");
                            break;
                        case 2:
                            barracima.setTitle("LuxVilla: Braga");
                            break;
                        case 3:
                            barracima.setTitle("LuxVilla: Porto");
                            break;
                    }
                    if (isNetworkAvailable(MainActivity.this)){

                    }else {
                        Snackbar.make(vwpgr,"Sem ligação há internet",Snackbar.LENGTH_LONG).setAction("ligar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent defenicoes = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(defenicoes);
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorAccent)).show();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        /*requestQueue= VolleySingleton.getInstancia(MainActivity.this).getRequestQueue();
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "http://brunomassa.esy.es/resultado.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(MainActivity.this,"Resposta: "+response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Erro: "+error,Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(stringRequest);*/

        Intent startServiceIntent = new Intent(this, notificationservice.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,startServiceIntent,0);


        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 300000, pendingIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem item=menu.findItem(R.id.procura);
        Drawable drawable = menu.findItem(R.id.procura).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.busca));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {

        vwpgr.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                barracima.setTitle("LuxVilla: Todas");
                break;
            case 1:
                barracima.setTitle("LuxVilla: Aveiro");
                break;
            case 2:
                barracima.setTitle("LuxVilla: Braga");
                break;
            case 3:
                barracima.setTitle("LuxVilla: Porto");
                break;
        }
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    public class adaptadorpaginas extends FragmentPagerAdapter{

        public adaptadorpaginas(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case SEPARADOR_TODAS:
                    separadortodas todas=new separadortodas();
                    return todas;
                case SEPARADOR_AVEIRO:
                    separadoraveiro aveiro=new separadoraveiro();
                    return aveiro;
                case SEPARADOR_BRAGA:
                    separadorbraga braga=new separadorbraga();
                    return braga;
                case SEPARADOR_PORTO:
                    separadorporto porto=new separadorporto();
                    return porto;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
