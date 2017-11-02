package com.example.massa.luxvilla.Actividades

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodas
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodasoffline
import com.example.massa.luxvilla.network.VolleySingleton
import com.example.massa.luxvilla.separadores.separadoraveiro
import com.example.massa.luxvilla.sqlite.BDAdapter
import com.example.massa.luxvilla.utils.*
import com.google.gson.Gson
import com.lapism.searchview.SearchAdapter
import com.lapism.searchview.SearchHistoryTable
import com.lapism.searchview.SearchItem
import com.lapism.searchview.SearchView
import kotlinx.android.synthetic.main.activity_searchableactivity.*
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

/**
 * Created by massa on 27/10/2017.
 */
class searchableactivity : AppCompatActivity(), RecyclerViewOnClickListenerHack {
    private var rvc1: RecyclerView? = null
    private var adaptador: adaptadorrvtodas? = null
    private var requestQueue: RequestQueue? = null
    private var casas = ArrayList<casas>()
    private var adaptadoroffline: adaptadorrvtodasoffline? = null
    internal var intent: Intent? = null
    internal var sugestions: MutableList<SearchItem>? = null
    internal var msearchHistoryTable: SearchHistoryTable? = null
    internal var sharedPreferencesapp: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchableactivity)
        sharedPreferencesapp = PreferenceManager.getDefaultSharedPreferences(this@searchableactivity)
        val nightmode = sharedPreferencesapp!!.getBoolean(resources.getString(R.string.night_mode), false)
        intent = getIntent()
        if (intent != null)
            query = intent!!.getStringExtra("query")


        val volleySingleton = VolleySingleton.getInstancia(this@searchableactivity)
        requestQueue = volleySingleton.requestQueue
        ctxtodas = this@searchableactivity

        if (query != null) {
            searchViewpresult.hint = query
        } else {
            searchViewpresult.setHint(R.string.app_hint)
            searchViewpresult.open(true)
        }

        if (nightmode) {
            searchViewpresult.setTheme(SearchView.THEME_DARK)
            searchViewpresult.setBackgroundColor(ContextCompat.getColor(this@searchableactivity, R.color.card_background))
            searchViewpresult.setIconColor(ContextCompat.getColor(this@searchableactivity, R.color.colorsearchicons))
        } else {
            searchViewpresult.setTheme(SearchView.THEME_LIGHT)
            searchViewpresult.setIconColor(ContextCompat.getColor(this@searchableactivity, R.color.colorsearchicons))
        }

        searchViewpresult.setVoice(true)
        searchViewpresult.setArrowOnly(true)
        searchViewpresult.setCursorDrawable(R.drawable.cursor)
        searchViewpresult.shouldClearOnClose = true

        searchViewpresult.setOnOpenCloseListener(object : com.lapism.searchview.SearchView.OnOpenCloseListener {
            override fun onClose(): Boolean {
                if (query != null) {
                    searchViewpresult.hint = query
                } else {
                    searchViewpresult.setHint(R.string.app_hint)
                }
                searchViewpresult.setTextStyle(1)
                return true
            }

            override fun onOpen(): Boolean {
                searchViewpresult.hint = resources.getString(R.string.app_hint)
                searchViewpresult.setTextStyle(0)
                return true
            }
        })

        searchViewpresult.setOnMenuClickListener {
            if (searchViewpresult.isSearchOpen) {
                searchViewpresult.close(true)
            } else {
                onBackPressed()
            }
        }

        sugestions = ArrayList()
        msearchHistoryTable = SearchHistoryTable(this@searchableactivity)
        searchViewpresult.setOnQueryTextListener(object : com.lapism.searchview.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(querysb: String): Boolean {
                query = querysb
                sugestions!!.add(SearchItem(query))
                msearchHistoryTable!!.addItem(SearchItem(query))
                if (isNetworkAvailable(this@searchableactivity)) {

                    adaptador = adaptadorrvtodas(this@searchableactivity)
                    rvc1!!.adapter = adaptador

                    sendjsonRequest()
                } else {
                    adaptadoroffline = adaptadorrvtodasoffline(this@searchableactivity, getdados())
                    rvc1!!.adapter = adaptadoroffline
                }
                searchViewpresult.close(true)
                return true
            }
        })
        val searchAdapter = SearchAdapter(this@searchableactivity, sugestions)
        searchAdapter.addOnItemClickListener { view, position ->
            val textView = view.findViewById<View>(R.id.textView_item_text) as TextView
            query = textView.text.toString()

            if (isNetworkAvailable(this@searchableactivity)) {

                adaptador = adaptadorrvtodas(this@searchableactivity)
                rvc1!!.adapter = adaptador

                sendjsonRequest()
            } else {
                adaptadoroffline = adaptadorrvtodasoffline(this@searchableactivity, getdados())
                rvc1!!.adapter = adaptadoroffline
            }

            searchViewpresult.close(true)
        }
        searchViewpresult.adapter = searchAdapter

        rvc1 = findViewById<View>(R.id.rv_search) as RecyclerView
        rvc1!!.layoutManager = LinearLayoutManager(this@searchableactivity)
        if (isNetworkAvailable(this@searchableactivity)) {

            adaptador = adaptadorrvtodas(this@searchableactivity)
            rvc1!!.adapter = adaptador

            sendjsonRequest()
        } else {
            adaptadoroffline = adaptadorrvtodasoffline(this@searchableactivity, getdados())
            rvc1!!.adapter = adaptadoroffline
        }

        swipesearch.setColorSchemeColors(ContextCompat.getColor(this@searchableactivity, R.color.colorPrimaryDark), ContextCompat.getColor(this@searchableactivity, R.color.colorPrimaryDark), ContextCompat.getColor(this@searchableactivity, R.color.colorPrimaryDark))
        swipesearch.setOnRefreshListener {
            if (isNetworkAvailable(this@searchableactivity)) {

                adaptador = adaptadorrvtodas(this@searchableactivity)
                rvc1!!.adapter = adaptador

                sendjsonRequest()
            } else {
                adaptadoroffline = adaptadorrvtodasoffline(this@searchableactivity, getdados())
                rvc1!!.adapter = adaptadoroffline
            }

            swipesearch.isRefreshing = false
        }

        rvc1!!.addOnItemTouchListener(RecyclerViewTouchListener(this@searchableactivity, rvc1!!, this))

    }

    private fun sendjsonRequest() {

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, "http://brunoferreira.esy.es/serverdata.php", null, Response.Listener { response ->
            casas = parsejsonResponse(response)
            adaptador!!.setCasas(casas)
        }, Response.ErrorListener { Snackbar.make(rvc1!!, "Falha ao ligar ao servidor", Snackbar.LENGTH_LONG).show() })

        requestQueue!!.add(jsonArrayRequest)
    }

    private fun parsejsonResponse(array: JSONArray?): ArrayList<casas> {
        val gson = Gson()
        val data : List<Todascasas> = gson.fromJson(array.toString(), Array<Todascasas>::class.java).toList()
        val casas = ArrayList<casas>()
        var loclowercase: String
        var querylowercase: String
        var preclowercase: String
        var infolowercase: String
        ids.clear()
        if (array != null) {

            for (todascasas : Todascasas in data){
                loclowercase = todascasas.local!!.toLowerCase()
                preclowercase = todascasas.preco!!.toLowerCase()
                infolowercase = todascasas.infocasa!!.toLowerCase()
                querylowercase = query!!.toLowerCase()
                if (loclowercase.contains(querylowercase) ||
                        preclowercase.contains(querylowercase) ||
                        infolowercase.contains(querylowercase)){
                    val casadata = com.example.massa.luxvilla.utils.casas()
                    casadata.id=todascasas.id
                    casadata.local=todascasas.local
                    casadata.preco=todascasas.preco
                    casadata.imgurl=todascasas.imgurl
                    val cs = listacasas()
                    cs.Local = todascasas.local
                    cs.Preço = todascasas.preco
                    cs.IMGurl = todascasas.imgurl
                    cs.info = todascasas.infocasa
                    cs.idcs = todascasas.id
                    ids.add(0,cs)
                    casas.add(0,casadata)
                }


            }

        }
        return casas
    }

    override fun onClickListener(view: View, position: Int) {
        val casas: List<listacasas>
        casas = ids
        val cs = casas[position]
        val infocasa = Intent(this@searchableactivity, casaactivity::class.java)
        infocasa.putExtra("localcasa", cs.Local)
        infocasa.putExtra("precocasa", cs.Preço)
        infocasa.putExtra("imgurl", cs.IMGurl)
        infocasa.putExtra("infocs", cs.info)
        infocasa.putExtra("csid", cs.idcs)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val iv = view.findViewById<View>(R.id.imgcasa)
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@searchableactivity, Pair.create(iv, "elementimg"))
            this@searchableactivity.startActivity(infocasa, optionsCompat.toBundle())
        } else {
            startActivity(infocasa)
        }
    }


    override fun onLongPressClickListener(view: View, position: Int) {

    }


    private class RecyclerViewTouchListener internal constructor(private val mContext: Context, rv: RecyclerView, private val mRecyclerViewOnClickListenerHack: RecyclerViewOnClickListenerHack?) : RecyclerView.OnItemTouchListener {
        private val mGestureDetector: GestureDetector

        init {

            mGestureDetector = GestureDetector(mContext, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent) {
                    super.onLongPress(e)

                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {

                    val cv = rv.findChildViewUnder(e.x, e.y)

                    var fav = false
                    if (cv is CardView) {
                        val x = (cv.getChildAt(0) as RelativeLayout).getChildAt(3).x
                        val w = (cv.getChildAt(0) as RelativeLayout).getChildAt(3).width.toFloat()
                        val y: Float// = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getY();
                        val h = (cv.getChildAt(0) as RelativeLayout).getChildAt(3).height.toFloat()

                        val rect = Rect()
                        (cv.getChildAt(0) as RelativeLayout).getChildAt(3).getGlobalVisibleRect(rect)
                        y = rect.top.toFloat()

                        if (e.x >= x && e.x <= w + x && e.rawY >= y && e.rawY <= h + y) {
                            fav = true
                        }
                    }


                    if (cv != null && mRecyclerViewOnClickListenerHack != null && !fav) {
                        mRecyclerViewOnClickListenerHack.onClickListener(cv,
                                rv.getChildAdapterPosition(cv))
                    }

                    return true
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            mGestureDetector.onTouchEvent(e)
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {}
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.defenicoes -> {
                val it = Intent(this@searchableactivity, settings::class.java)
                startActivity(it)
            }
            R.id.procura -> searchViewpresult.open(true)
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        internal var query: String? = null
        internal var ids: ArrayList<listacasas> = ArrayList()
        internal var adapter: BDAdapter? = null
        internal var ctxtodas: Context? = null

        fun getdados(): List<listasql> {

            val dados = ArrayList<listasql>()

            adapter = BDAdapter(ctxtodas!!)
            val colunas = adapter!!.numerodecolunas()
            ids.clear()

            var loclowercase: String
            var querylowercase: String
            var preclowercase: String
            var infolowercase: String


            for (i in 0 until colunas) {
                val txtexato = listasql()
                val locsqloffline = adapter!!.verlocais((i + 1).toString())
                val precsqloffline = adapter!!.verprecos((i + 1).toString())
                val infossqloffline = adapter!!.verinfos((i + 1).toString())
                val id = (i + 1).toString()

                loclowercase = locsqloffline!!.toLowerCase()
                preclowercase = precsqloffline!!.toLowerCase()
                infolowercase = infossqloffline!!.toLowerCase()
                querylowercase = query!!.toLowerCase()
                if (loclowercase.contains(querylowercase) || preclowercase.contains(querylowercase) || infolowercase.contains(querylowercase)) {
                    txtexato.Loc = locsqloffline
                    txtexato.Prec = precsqloffline
                    txtexato.Inf = infossqloffline
                    txtexato.Id = id
                    dados.add(0, txtexato)
                    val cs = listacasas()
                    cs.Local = locsqloffline
                    cs.Preço = precsqloffline
                    cs.info = infossqloffline
                    cs.idcs = id
                    ids.add(0, cs)
                }
            }
            return dados
        }
    }
}
