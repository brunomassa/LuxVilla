package com.example.massa.luxvilla.separadores;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.massa.luxvilla.Actividades.casaactivity;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodas;
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodasoffline;
import com.example.massa.luxvilla.network.VolleySingleton;
import com.example.massa.luxvilla.sqlite.BDAdapter;
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
 * Use the {@link separadorbraga#newInstance} factory method to
 * create an instance of this fragment.
 */
public class separadorbraga extends Fragment implements RecyclerViewOnClickListenerHack {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private ArrayList<todascasas> casas=new ArrayList<>();
    RecyclerView recyclerViewtodas;
    private adaptadorrvtodas adaptador;
    SwipeRefreshLayout swipeRefreshLayout;
    static ArrayList<listacasas> ids=new ArrayList();
    static BDAdapter adapter;
    private adaptadorrvtodasoffline adaptadoroffline;
    static Context ctxtodas;


    public separadorbraga() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment separadorbraga.
     */
    // TODO: Rename and change types and number of parameters
    public static separadorbraga newInstance(String param1, String param2) {
        separadorbraga fragment = new separadorbraga();
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

        volleySingleton=VolleySingleton.getInstancia(getActivity());
        requestQueue=volleySingleton.getRequestQueue();
        ctxtodas=getContext();
    }

    private void sendjsonRequest(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, "http://brunomassa.esy.es/resultado.json", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                //Toast.makeText(getActivity(),"Resposta: "+response,Toast.LENGTH_LONG).show();
                casas=parsejsonResponse(response);
                adaptador.setCasas(casas);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar.make(recyclerViewtodas,"Falha ao ligar ao servidor",Snackbar.LENGTH_LONG).show();

            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private ArrayList<todascasas> parsejsonResponse(JSONArray array){
        ArrayList<todascasas> casas=new ArrayList<>();
        ids.clear();
        if (array!=null||array.length()>0){
            for (int i=2;i<5;i++){
                try {
                    JSONObject casaexata=array.getJSONObject(i);
                    String id=casaexata.getString(keys.allkeys.KEY_ID);
                    String local=casaexata.getString(keys.allkeys.KEY_LOCAL);
                    String preco=casaexata.getString(keys.allkeys.KEY_PRECO);
                    String imgurl=casaexata.getString(keys.allkeys.KEY_IMGURL);
                    String info=casaexata.getString(keys.allkeys.KEY_INFO);
                    if (local.equalsIgnoreCase("Braga")) {
                        todascasas casasadd = new todascasas();
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

                    Snackbar.make(recyclerViewtodas, "Falha ao ligar ao servidor", Snackbar.LENGTH_LONG).show();
                }
            }

        }

        //Toast.makeText(getActivity(),casas.toString(),Toast.LENGTH_LONG).show();
        return casas;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_separadorbraga, container, false);
        recyclerViewtodas=(RecyclerView)view.findViewById(R.id.rvbraga);


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

        if (isNetworkAvailable(getActivity())) {

            adaptador=new adaptadorrvtodas(getActivity());
            recyclerViewtodas.setAdapter(adaptador);

            sendjsonRequest();
        } else {
            adaptadoroffline=new adaptadorrvtodasoffline(getActivity(),getdados());
            recyclerViewtodas.setAdapter(adaptadoroffline);
        }

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipebraga);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(getActivity())) {

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
            String id=String.valueOf(i + 1);
            if (locsqloffline.equalsIgnoreCase("Braga")){
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
            }else {

            }

        }
        return dados;
    }
}
