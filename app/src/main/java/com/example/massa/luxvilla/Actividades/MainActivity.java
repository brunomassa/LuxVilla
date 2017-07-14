package com.example.massa.luxvilla.Actividades;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.separadores.separadoraveiro;
import com.example.massa.luxvilla.separadores.separadorbraga;
import com.example.massa.luxvilla.separadores.separadorporto;
import com.example.massa.luxvilla.separadores.separadortodas;
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ViewPager vwpgr;
    TabLayout tbs;
    final int SEPARADOR_TODAS=0;
    final int SEPARADOR_AVEIRO=1;
    final int SEPARADOR_BRAGA=2;
    final int SEPARADOR_PORTO=3;
    BDAdapter adapter;
    SharedPreferences sharedPreferencesnotification;
    com.lapism.searchview.SearchView searchViewpr;
    List<SearchItem> sugestions;
    SearchHistoryTable msearchHistoryTable;
    AppBarLayout appBarLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    TextView tvusername;
    TextView tvusermail;
    CircleImageView ivprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            Transition tr=new ChangeBounds();
            getWindow().setSharedElementExitTransition(tr);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesnotification= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean notificationstate=sharedPreferencesnotification.getBoolean(getResources().getString(R.string.notificaçãoes),true);
        if (notificationstate){
            FirebaseMessaging.getInstance().subscribeToTopic("todos");
        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic("todos");
        }


        searchViewpr=(com.lapism.searchview.SearchView)findViewById(R.id.searchViewp);
        searchViewpr.setHint("LuxVilla: Todas");
        searchViewpr.setTextSize(18);
        searchViewpr.setVoice(true);
        searchViewpr.setTextStyle(1);
        searchViewpr.setCursorDrawable(R.drawable.cursor);
        searchViewpr.setIconColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
        searchViewpr.setShouldClearOnClose(true);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawerll);
        navigationView=(NavigationView)findViewById(R.id.navigationview);

        tbs=(TabLayout)findViewById(R.id.tabs);
        tbs.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent));
        vwpgr= (ViewPager) findViewById(R.id.vpgr);
        adapter=new BDAdapter(this);


        tbs.setupWithViewPager(vwpgr);



        int numerocolunas=adapter.numerodecolunas();
        if (numerocolunas==0 && !isNetworkAvailable(this)){
            vwpgr.setVisibility(View.GONE);
            tbs.setVisibility(View.GONE);
        }else {
            adaptadorpaginas adaptador=new adaptadorpaginas(getSupportFragmentManager());
            vwpgr.setAdapter(adaptador);

            vwpgr.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

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
                    if (!isNetworkAvailable(MainActivity.this)){
                        Snackbar.make(vwpgr,"Sem ligação há internet",Snackbar.LENGTH_LONG).setAction("ligar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent defenicoes = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(defenicoes);
                            }
                        }).setActionTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent)).show();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        sugestions= new ArrayList<>();
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
        searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
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

        View headerLayout = navigationView.getHeaderView(0);
        ivprofile=(CircleImageView) headerLayout.findViewById(R.id.profileimage);
        tvusername=(TextView) headerLayout.findViewById(R.id.textviewusername);
        tvusermail=(TextView) headerLayout.findViewById(R.id.textviewusermail);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_item_1:
                        vwpgr.setCurrentItem(0);
                        break;
                    case R.id.navigation_item_2:
                        vwpgr.setCurrentItem(1);
                        break;
                    case R.id.navigation_item_3:
                        vwpgr.setCurrentItem(2);
                        break;
                    case R.id.navigation_item_4:
                        vwpgr.setCurrentItem(3);
                        break;
                    case R.id.navigation_subheader_1:
                        Intent it=new Intent(MainActivity.this, settings.class);
                        startActivity(it);
                        break;
                    case R.id.navigation_subheader_profile:
                        //TODO: profile
                        startActivity(new Intent(MainActivity.this, Userprofile.class));
                        break;
                    case R.id.navigation_subheader_signout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this,Loginactivity.class));
                        finish();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });


        ShortcutManager shortcutManager;

        ShortcutInfo shortcut;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);

            shortcut = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel(getResources().getString(R.string.busca))
                    .setLongLabel(getResources().getString(R.string.busca))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.ic_shortcut_search))
                    .setIntents(
                            new Intent[]{
                                    new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                    new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, searchableactivity.class)
                            })
                    .build();
            ShortcutInfo shortcutInfoLink = new ShortcutInfo.Builder(this, "shortcut_web")
                    .setShortLabel("website")
                    .setLongLabel("LuxVilla website")
                    .setIcon(Icon.createWithResource(this, R.drawable.logo))
                    .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://brunoferreira.esy.es/")))
                    .build();


            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut, shortcutInfoLink));
        }

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private class adaptadorpaginas extends FragmentPagerAdapter{

        adaptadorpaginas(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case SEPARADOR_TODAS:
                    return new separadortodas();
                case SEPARADOR_AVEIRO:
                    return new separadoraveiro();
                case SEPARADOR_BRAGA:
                    return new separadorbraga();
                case SEPARADOR_PORTO:
                    return new separadorporto();
            }
            return null;
        }

        public CharSequence getPageTitle(int position){
            String[] tabsname={"TODAS","AVEIRO","BRAGA","PORTO"};

            return tabsname[position];
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        firebaseutils.getuserdata(MainActivity.this,tvusername,tvusermail, ivprofile);
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
