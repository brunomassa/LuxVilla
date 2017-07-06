package com.example.massa.luxvilla.separadores;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.massa.luxvilla.Actividades.casaactivity;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodas;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodasoffline;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.sqlite.BDAdapter;
import com.example.massa.luxvilla.utils.NetworkCheck;
import com.example.massa.luxvilla.utils.RecyclerViewOnClickListenerHack;
import com.example.massa.luxvilla.utils.keys;
import com.example.massa.luxvilla.utils.listacasas;
import com.example.massa.luxvilla.utils.listasql;
import com.example.massa.luxvilla.utils.todascasas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link separadortodas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class separadortodas extends Fragment implements RecyclerViewOnClickListenerHack {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RequestQueue requestQueue;
    private ArrayList<todascasas> casas=new ArrayList<>();
    RecyclerView recyclerViewtodas;
    private adaptadorrvtodas adaptador;
    SwipeRefreshLayout swipeRefreshLayout;
    static ArrayList<listacasas> ids=new ArrayList();
    static BDAdapter adapter;
    private adaptadorrvtodasoffline adaptadoroffline;
    static Context ctxtodas;
    ProgressBar progressBar;



    public separadortodas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment separadortodas.
     */
    // TODO: Rename and change types and number of parameters
    public static separadortodas newInstance(String param1, String param2) {
        separadortodas fragment = new separadortodas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        VolleySingleton volleySingleton = VolleySingleton.getInstancia(getActivity());
        requestQueue= volleySingleton.getRequestQueue();
        ctxtodas=getContext();

    }

    private void sendjsonRequest(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,"http://brunoferreira.esy.es/serverdata.php",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                casas=parsejsonResponse(response);
                adaptador.setCasas(casas);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                Snackbar.make(recyclerViewtodas,"Falha ao ligar ao servidor",Snackbar.LENGTH_LONG).show();

            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private ArrayList<todascasas> parsejsonResponse(JSONArray array){
        ArrayList<todascasas> casas=new ArrayList<>();

        ids.clear();
        if (array != null){

            adapter=new BDAdapter(getActivity());

            for (int i=0;i<array.length();i++){
                try {
                    JSONObject casaexata=array.getJSONObject(i);
                    String id=casaexata.getString(keys.allkeys.KEY_ID);
                    String local=casaexata.getString(keys.allkeys.KEY_LOCAL);
                    String preco=casaexata.getString(keys.allkeys.KEY_PRECO);
                    String imgurl=casaexata.getString(keys.allkeys.KEY_IMGURL);
                    String info=casaexata.getString(keys.allkeys.KEY_INFO);
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


                    String locsql=adapter.verlocais(id);
                    String precsql=adapter.verprecos(id);
                    String infossql=adapter.verinfos(id);
                    if (!local.equalsIgnoreCase(locsql) && !preco.equalsIgnoreCase(precsql) && !info.equalsIgnoreCase(infossql)){
                        long longid=adapter.inserirdados(local,preco,info);
                        if (longid>0){
                            Toast.makeText(getActivity(), "Casa adicionada ao modo offline", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getActivity(), "Ocorreu um erro, tente novamente mais tarde", Toast.LENGTH_LONG).show();
                        }
                    }
                    casas.add(0,casasadd);
                    ids.add(0,cs);

                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Falha ao ligar ao servidor", Toast.LENGTH_LONG).show();
                }
            }

        }
        return casas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_separadortodas, container, false);

        recyclerViewtodas=(RecyclerView)view.findViewById(R.id.rvtodas);
        progressBar=(ProgressBar)view.findViewById(R.id.progress_bar);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe);



        TelephonyManager manager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            //tablet
            final int rotation = ((WindowManager) ctxtodas.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false));
                    break;
                case Surface.ROTATION_90:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
                    break;
                case Surface.ROTATION_180:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false));
                    break;
                default:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),4,GridLayoutManager.VERTICAL,false));
                    break;
            }
        }else{
            //phone
            final int rotation = ((WindowManager) ctxtodas.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    recyclerViewtodas.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                case Surface.ROTATION_90:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false));
                    break;
                case Surface.ROTATION_180:
                    recyclerViewtodas.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                default:
                    recyclerViewtodas.setLayoutManager(new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false));
                    break;
            }
        }





        if (NetworkCheck.isNetworkAvailable(getActivity())) {

            adaptador=new adaptadorrvtodas(getActivity());
            recyclerViewtodas.setAdapter(adaptador);

            sendjsonRequest();
        } else {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            adaptadoroffline=new adaptadorrvtodasoffline(getActivity(),getdados());
            recyclerViewtodas.setAdapter(adaptadoroffline);
        }


        //SWIPE
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkCheck.isNetworkAvailable(getActivity())) {

                    adaptador=new adaptadorrvtodas(getActivity());
                    recyclerViewtodas.setAdapter(adaptador);

                    sendjsonRequest();
                } else {
                    adaptadoroffline=new adaptadorrvtodasoffline(getActivity(),getdados());
                    recyclerViewtodas.setAdapter(adaptadoroffline);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerViewtodas.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerViewtodas, this));



        return view;
    }

    @Override
    public void onClickListener(View view, int position) {
        List<listacasas> casas;
        casas = ids;
        listacasas cs = casas.get(position);
        Intent infocasa = new Intent(getActivity(), casaactivity.class);
        infocasa.putExtra("localcasa", cs.Local);
        infocasa.putExtra("precocasa", cs.Preço);
        infocasa.putExtra("imgurl", cs.IMGurl);
        infocasa.putExtra("infocs", cs.info);
        infocasa.putExtra("csid",cs.idcs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View iv = view.findViewById(R.id.imgcasa);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), Pair.create(iv, "elementimg"));
            getActivity().startActivity(infocasa, optionsCompat.toBundle());
        } else {
            startActivity(infocasa);
        }
    }


    @Override
    public void onLongPressClickListener(View view, int position) {

    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        RecyclerViewTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListenerHack rvoclh){
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
                    if( cv instanceof CardView ){
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

    public static List<listasql> getdados(){

        List<listasql>dados=new ArrayList<>();

        adapter=new BDAdapter(ctxtodas);
        int colunas=adapter.numerodecolunas();
        ids.clear();


        for(int i=0;i<colunas;i++){
            listasql txtexato=new listasql();
            String locsqloffline=adapter.verlocais(String.valueOf(i+1));
            String precsqloffline=adapter.verprecos(String.valueOf(i + 1));
            String infossqloffline=adapter.verinfos(String.valueOf(i + 1));
            String id=String.valueOf(i+1);
            txtexato.Loc=locsqloffline;
            txtexato.Prec=precsqloffline;
            txtexato.Inf=infossqloffline;
            txtexato.Id=id;
            dados.add(0,txtexato);
            listacasas cs=new listacasas();
            cs.Local=locsqloffline;
            cs.Preço=precsqloffline;
            cs.info=infossqloffline;
            cs.idcs=id;
            ids.add(0,cs);
        }
        return dados;
    }

}
