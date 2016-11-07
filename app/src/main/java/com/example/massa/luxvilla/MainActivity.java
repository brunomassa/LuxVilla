package com.example.massa.luxvilla;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.massa.luxvilla.Actividades.SettingsActivity;
import com.example.massa.luxvilla.separadores.separadoraveiro;
import com.example.massa.luxvilla.separadores.separadorbraga;
import com.example.massa.luxvilla.separadores.separadorporto;
import com.example.massa.luxvilla.separadores.separadortodas;
import com.example.massa.luxvilla.services.notificationservice;
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    BDAdapter adapter;
    SharedPreferences sharedPreferences;
    final String ISOPENAPP="appstate";
    SharedPreferences.Editor editor;
    com.lapism.searchview.SearchView searchViewpr;
    List<SearchItem> sugestions;
    SearchHistoryTable msearchHistoryTable;
    AppBarLayout appBarLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            getWindow().setSharedElementExitTransition(new ChangeBounds());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=getSharedPreferences(ISOPENAPP, 0);
        editor=sharedPreferences.edit();
        editor.putInt("open", 1);
        editor.apply();

        searchViewpr=(com.lapism.searchview.SearchView)findViewById(R.id.searchViewp);
        searchViewpr.setHint("LuxVilla: Todas");
        searchViewpr.setTextSize(18);
        searchViewpr.setVoice(false);
        searchViewpr.setTextStyle(1);
        searchViewpr.setCursorDrawable(R.drawable.cursor);
        searchViewpr.setIconColor(getResources().getColor(R.color.colorPrimary));
        searchViewpr.setShouldClearOnClose(true);


        barracima=(Toolbar)findViewById(R.id.brcima);
        barracima.setTitle("");
        setSupportActionBar(barracima);
        tabs= (MaterialTabHost) findViewById(R.id.materialTabHost);
        tabs.addTab(tabs.newTab().setText("TODAS").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("AVEIRO").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("BRAGA").setTabListener(this));
        tabs.addTab(tabs.newTab().setText("PORTO").setTabListener(this));
        vwpgr= (ViewPager) findViewById(R.id.vpgr);
        adapter=new BDAdapter(this);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawerll);
        navigationView=(NavigationView)findViewById(R.id.navigationview);

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
                            searchViewpr.setHint("LuxVilla: Todas");
                            navigationView.getMenu().getItem(0).setChecked(true);
                            break;
                        case 1:
                            searchViewpr.setHint("LuxVilla: Aveiro");
                            navigationView.getMenu().getItem(1).setChecked(true);
                            break;
                        case 2:
                            searchViewpr.setHint("LuxVilla: Braga");
                            navigationView.getMenu().getItem(2).setChecked(true);
                            break;
                        case 3:
                            searchViewpr.setHint("LuxVilla: Porto");
                            navigationView.getMenu().getItem(3).setChecked(true);
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

        sugestions=new ArrayList<SearchItem>();
        msearchHistoryTable=new SearchHistoryTable(MainActivity.this);
        msearchHistoryTable.setHistorySize(3);
        searchViewpr.setOnQueryTextListener(new com.lapism.searchview.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                sugestions.add(new SearchItem(query));
                msearchHistoryTable.addItem(new SearchItem(query));


                Intent search=new Intent(MainActivity.this,searchableactivity.class);
                search.putExtra("query",query);
                startActivity(search);
                searchViewpr.setShouldClearOnClose(true);
                searchViewpr.close(true);
                return true;
            }
        });
        final SearchAdapter searchAdapter=new SearchAdapter(MainActivity.this,sugestions);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                String query = textView.getText().toString();

                Intent search=new Intent(MainActivity.this,searchableactivity.class);
                search.putExtra("query",query);
                startActivity(search);
                searchViewpr.close(true);
            }
        });
        searchViewpr.setAdapter(searchAdapter);
        searchViewpr.setOnMenuClickListener(new com.lapism.searchview.SearchView.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                drawerLayout.openDrawer(navigationView);
            }
        });

        appBarLayout=(AppBarLayout)findViewById(R.id.appbarll);
        /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset==0){
                    searchViewpr.setVisibility(View.VISIBLE);
                }else {
                    searchViewpr.setVisibility(View.INVISIBLE);
                }
            }
        });*/
        searchViewpr.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
            @Override
            public boolean onClose() {
                int id=vwpgr.getCurrentItem();
                switch (id){
                    case 0:
                        searchViewpr.setHint("LuxVilla: Todas");
                        break;
                    case 1:
                        searchViewpr.setHint("LuxVilla: Aveiro");
                        break;
                    case 2:
                        searchViewpr.setHint("LuxVilla: Braga");
                        break;
                    case 3:
                        searchViewpr.setHint("LuxVilla: Porto");
                        break;
                }
                searchViewpr.setTextStyle(1);
                return true;
            }

            @Override
            public boolean onOpen() {
                searchViewpr.setHint(getResources().getString(R.string.app_hint));
                searchViewpr.setTextStyle(0);
                return true;
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_item_1:
                        tabs.setSelectedNavigationItem(0);
                        vwpgr.setCurrentItem(0);
                        break;
                    case R.id.navigation_item_2:
                        tabs.setSelectedNavigationItem(1);
                        vwpgr.setCurrentItem(1);
                        break;
                    case R.id.navigation_item_3:
                        tabs.setSelectedNavigationItem(2);
                        vwpgr.setCurrentItem(2);
                        break;
                    case R.id.navigation_item_4:
                        tabs.setSelectedNavigationItem(3);
                        vwpgr.setCurrentItem(3);
                        break;
                    case R.id.navigation_subheader_1:
                        Intent it=new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(it);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        boolean isalarmactive=(PendingIntent.getService(this,0,new Intent(this, notificationservice.class),PendingIntent.FLAG_NO_CREATE)== null);

        if (isalarmactive){
            Intent startServiceIntent = new Intent(this, notificationservice.class);
            PendingIntent pendingIntent=PendingIntent.getService(this,0,startServiceIntent,0);


            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 5);

            AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 300000, pendingIntent);
        }

    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        editor.putInt("open",0);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

       /* switch (item.getItemId()){
            case R.id.defenicoes:

                Intent it=new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(it);
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {

        vwpgr.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()){
            case 0:
                searchViewpr.setHint("LuxVilla: Todas");
                //navigationView.getMenu().getItem(0).setChecked(true);
                break;
            case 1:
                searchViewpr.setHint("LuxVilla: Aveiro");
                //navigationView.getMenu().getItem(1).setChecked(true);
                break;
            case 2:
                searchViewpr.setHint("LuxVilla: Braga");
               // navigationView.getMenu().getItem(2).setChecked(true);
                break;
            case 3:
                searchViewpr.setHint("LuxVilla: Porto");
                //navigationView.getMenu().getItem(3).setChecked(true);
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

    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawer(navigationView);
        }else {
            super.onBackPressed();
        }

    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
