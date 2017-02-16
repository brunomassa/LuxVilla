package com.example.massa.luxvilla;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.massa.luxvilla.Actividades.SettingsActivity;
import com.example.massa.luxvilla.Actividades.casaactivity;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodas;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodasoffline;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.example.massa.luxvilla.sugestoes.SearchSugestionsProvider;
import com.example.massa.luxvilla.utils.RecyclerViewOnClickListenerHack;
import com.example.massa.luxvilla.utils.keys;
import com.example.massa.luxvilla.utils.listacasas;
import com.example.massa.luxvilla.utils.listasql;
import com.example.massa.luxvilla.utils.todascasas;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class searchableactivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {
    private RecyclerView rvc1;
    private adaptadorrvtodas adaptador;
    private RequestQueue requestQueue;
    private ArrayList<todascasas> casas=new ArrayList<>();
    private VolleySingleton volleySingleton;
    SwipeRefreshLayout swipeRefreshLayout;
    static String query=null;
    static ArrayList<listacasas> ids=new ArrayList();
    static BDAdapter adapter;
    private adaptadorrvtodasoffline adaptadoroffline;
    static Context ctxtodas;
    Intent intent;
    com.lapism.searchview.SearchView searchViewpr;
    List<SearchItem> sugestions;
    SearchHistoryTable msearchHistoryTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchableactivity);
        intent = getIntent();
        if (intent!=null)
        query = intent.getStringExtra("query");



        volleySingleton = VolleySingleton.getInstancia(searchableactivity.this);
        requestQueue = volleySingleton.getRequestQueue();
        ctxtodas = searchableactivity.this;

        searchViewpr = (com.lapism.searchview.SearchView) findViewById(R.id.searchViewpresult);
        if (query!=null) {
            searchViewpr.setHint(query);
        }else {
            searchViewpr.setHint(R.string.app_hint);
        }
        searchViewpr.setVoice(false);
        searchViewpr.setArrowOnly(true);
        searchViewpr.setIconColor(getResources().getColor(R.color.colorPrimary));
        searchViewpr.setCursorDrawable(R.drawable.cursor);

        searchViewpr.setOnOpenCloseListener(new com.lapism.searchview.SearchView.OnOpenCloseListener() {
            @Override
            public boolean onClose() {
                if (query!=null) {
                    searchViewpr.setHint(query);
                }else {
                    searchViewpr.setHint(R.string.app_hint);
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

        /*searchViewpr.setOnOpenCloseListener(new com.lapism.searchview.SearchView.OnOpenCloseListener() {
            @Override
            public void onClose() {
                searchViewpr.setHint(query);
                searchViewpr.setTextStyle(1);
            }

            @Override
            public void onOpen() {
                searchViewpr.setHint(getResources().getString(R.string.app_hint));
                searchViewpr.setTextStyle(0);
            }
        });*/
        searchViewpr.setOnMenuClickListener(new com.lapism.searchview.SearchView.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                if (searchViewpr.isSearchOpen()) {
                    searchViewpr.close(true);
                } else {
                    onBackPressed();
                }
            }
        });

        sugestions = new ArrayList<SearchItem>();
        msearchHistoryTable = new SearchHistoryTable(searchableactivity.this);
        searchViewpr.setOnQueryTextListener(new com.lapism.searchview.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String querysb) {
                query = querysb;
                sugestions.add(new SearchItem(query));
                msearchHistoryTable.addItem(new SearchItem(query));
                if (isNetworkAvailable(searchableactivity.this)) {

                    adaptador = new adaptadorrvtodas(searchableactivity.this);
                    rvc1.setAdapter(adaptador);

                    sendjsonRequest();
                } else {
                    adaptadoroffline = new adaptadorrvtodasoffline(searchableactivity.this, getdados());
                    rvc1.setAdapter(adaptadoroffline);
                }
                searchViewpr.close(true);
                return true;
            }
        });
        SearchAdapter searchAdapter = new SearchAdapter(searchableactivity.this, sugestions);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                query = textView.getText().toString();

                if (isNetworkAvailable(searchableactivity.this)) {

                    adaptador = new adaptadorrvtodas(searchableactivity.this);
                    rvc1.setAdapter(adaptador);

                    sendjsonRequest();
                } else {
                    adaptadoroffline = new adaptadorrvtodasoffline(searchableactivity.this, getdados());
                    rvc1.setAdapter(adaptadoroffline);
                }

                searchViewpr.close(true);
            }
        });
        searchViewpr.setAdapter(searchAdapter);

        rvc1 = (RecyclerView) findViewById(R.id.rv_search);
        rvc1.setLayoutManager(new LinearLayoutManager(searchableactivity.this));
        if (isNetworkAvailable(searchableactivity.this)) {

            adaptador = new adaptadorrvtodas(searchableactivity.this);
            rvc1.setAdapter(adaptador);

            sendjsonRequest();
        } else {
            adaptadoroffline = new adaptadorrvtodasoffline(searchableactivity.this, getdados());
            rvc1.setAdapter(adaptadoroffline);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipesearch);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(searchableactivity.this)) {

                    adaptador = new adaptadorrvtodas(searchableactivity.this);
                    rvc1.setAdapter(adaptador);

                    sendjsonRequest();
                } else {
                    adaptadoroffline = new adaptadorrvtodasoffline(searchableactivity.this, getdados());
                    rvc1.setAdapter(adaptadoroffline);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rvc1.addOnItemTouchListener(new RecyclerViewTouchListener(searchableactivity.this, rvc1, this));

    }

    private void sendjsonRequest(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, "http://brunoferreira.esy.es/resultado.json", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                //Toast.makeText(getActivity(),"Resposta: "+response,Toast.LENGTH_LONG).show();
                casas=parsejsonResponse(response);
                adaptador.setCasas(casas);
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
        ids.clear();
        String loclowercase="";
        String querylowercase="";
        String preclowercase="";
        String infolowercase="";
        if (array!=null||array.length()>0){
            for (int i=0;i<array.length();i++){
                try {
                    JSONObject casaexata=array.getJSONObject(i);
                    String id=casaexata.getString(keys.allkeys.KEY_ID);
                    String local=casaexata.getString(keys.allkeys.KEY_LOCAL);
                    String preco=casaexata.getString(keys.allkeys.KEY_PRECO);
                    String imgurl=casaexata.getString(keys.allkeys.KEY_IMGURL);
                    String info=casaexata.getString(keys.allkeys.KEY_INFO);

                    if (query!=null){
                        loclowercase=local.toLowerCase();
                        preclowercase=preco.toLowerCase();
                        infolowercase=info.toLowerCase();
                        querylowercase=query.toLowerCase();
                        if (loclowercase.contains(querylowercase) || preclowercase.contains(querylowercase) || infolowercase.contains(querylowercase)){
                            todascasas casasadd=new todascasas();
                            casasadd.setLOCAL(local);
                            casasadd.setPRECO(preco);
                            casasadd.setIMGURL(imgurl);
                            casasadd.setID(id);
                            listacasas cs=new listacasas();
                            cs.Local=local;
                            cs.Preço=preco;
                            cs.IMGurl=imgurl;
                            cs.info=info;
                            cs.idcs=id;
                            ids.add(0,cs);

                            casas.add(0,casasadd);
                        }
                    }else {
                        todascasas casasadd=new todascasas();
                        casasadd.setLOCAL(local);
                        casasadd.setPRECO(preco);
                        casasadd.setIMGURL(imgurl);
                        casasadd.setID(id);
                        listacasas cs=new listacasas();
                        cs.Local=local;
                        cs.Preço=preco;
                        cs.IMGurl=imgurl;
                        cs.info=info;
                        cs.idcs=id;
                        ids.add(0,cs);

                        casas.add(0,casasadd);
                    }


                    //Toast.makeText(getActivity(),casas.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {

                }
            }

        }

        //Toast.makeText(getActivity(),casas.toString(),Toast.LENGTH_LONG).show();
        return casas;
    }

    @Override
    public void onClickListener(View view, int position) {
        List<listacasas> casas;
        casas=ids;
        listacasas cs=casas.get(position);
        Intent infocasa = new Intent(searchableactivity.this, casaactivity.class);
        infocasa.putExtra("localcasa", cs.Local);
        infocasa.putExtra("precocasa", cs.Preço);
        infocasa.putExtra("imgurl", cs.IMGurl);
        infocasa.putExtra("infocs", cs.info);
        infocasa.putExtra("csid", cs.idcs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View iv=view.findViewById(R.id.imgcasa);
            ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(searchableactivity.this, Pair.create(iv, "elementimg"));
            searchableactivity.this.startActivity(infocasa, optionsCompat.toBundle());
        } else {
            startActivity(infocasa);
        }
    }


    @Override
    public void onLongPressClickListener(View view, int position) {

    }


    public static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListenerHack rvoclh){
            mContext = c;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    boolean fav = false;
                    if( cv instanceof CardView){
                        float x = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getX();
                        float w = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getWidth();
                        float y;// = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getY();
                        float h = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getHeight();

                        Rect rect = new Rect();
                        ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getGlobalVisibleRect(rect);
                        y = rect.top;

                        if( e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y ){
                            fav = true;
                        }
                    }


                    if(cv != null && mRecyclerViewOnClickListenerHack != null && !fav){
                        mRecyclerViewOnClickListenerHack.onClickListener(cv,
                                rv.getChildAdapterPosition(cv) );
                    }

                    return(true);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_search, menu);


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.defenicoes:
                Intent it=new Intent(searchableactivity.this, SettingsActivity.class);
                startActivity(it);
                break;
            case R.id.procura:
                searchViewpr.open(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static List<listasql> getdados(){

        List<listasql>dados=new ArrayList<>();

        adapter=new BDAdapter(ctxtodas);
        int colunas=adapter.numerodecolunas();
        ids.clear();

        String loclowercase;
        String querylowercase;
        String preclowercase;
        String infolowercase;


        for(int i=0;i<colunas;i++){
            listasql txtexato=new listasql();
            String locsqloffline=adapter.verlocais(String.valueOf(i + 1));
            String precsqloffline=adapter.verprecos(String.valueOf(i + 1));
            String infossqloffline=adapter.verinfos(String.valueOf(i + 1));
            String id=String.valueOf(i + 1);

            loclowercase=locsqloffline.toLowerCase();
            preclowercase=precsqloffline.toLowerCase();
            infolowercase=infossqloffline.toLowerCase();
            querylowercase=query.toLowerCase();
            if (loclowercase.contains(querylowercase) || preclowercase.contains(querylowercase) || infolowercase.contains(querylowercase)) {
                txtexato.Loc = locsqloffline;
                txtexato.Prec = precsqloffline;
                txtexato.Inf = infossqloffline;
                txtexato.Id=id;
                dados.add(0,txtexato);
                listacasas cs = new listacasas();
                cs.Local = locsqloffline;
                cs.Preço = precsqloffline;
                cs.info = infossqloffline;
                cs.idcs=id;
                ids.add(0,cs);
            }else {

            }
        }
        return dados;
    }
}
