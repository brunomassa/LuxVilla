package com.example.massa.luxvilla.separadores

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.TelephonyManager
import android.view.*
import android.widget.RelativeLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.example.massa.luxvilla.Actividades.casaactivity
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodas
import com.example.massa.luxvilla.adaptadores.adaptadorrvtodasoffline
import com.example.massa.luxvilla.network.VolleySingleton
import com.example.massa.luxvilla.sqlite.BDAdapter
import com.example.massa.luxvilla.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_separadores.*
import kotlinx.android.synthetic.main.layout_separadores.view.*
import org.json.JSONArray
import java.util.*

/**
 * Created by massa on 27/10/2017.
 */


class separadorbraga : Fragment(), RecyclerViewOnClickListenerHack {

    private val imageLoader: ImageLoader? = null
    private var requestQueue: RequestQueue? = null
    private var casas = ArrayList<casas>()
    private var adaptador: adaptadorrvtodas? = null
    private var adaptadoroffline: adaptadorrvtodasoffline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val mParam1 = arguments.getString(ARG_PARAM1)
            val mParam2 = arguments.getString(ARG_PARAM2)
        }

        val volleySingleton = VolleySingleton.getInstancia(activity)
        requestQueue = volleySingleton.requestQueue
        ctxtodas = context
    }

    private fun sendjsonRequest() {

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, "http://brunoferreira.esy.es/serverdata.php", null, Response.Listener { response ->
            casas = parsejsonResponse(response)
            adaptador!!.setCasas(casas)
            progress_bar.visibility = View.GONE
            swipe.visibility = View.VISIBLE
        }, Response.ErrorListener {
            progress_bar.visibility = View.GONE
            swipe.visibility = View.VISIBLE
            Snackbar.make(rvcasas, "Falha ao ligar ao servidor", Snackbar.LENGTH_LONG).show()
        })

        requestQueue!!.add(jsonArrayRequest)
    }

    private fun parsejsonResponse(array: JSONArray?): ArrayList<casas> {
        val gson = Gson()
        val data : List<Todascasas> = gson.fromJson(array.toString(), Array<Todascasas>::class.java).toList()
        val casas = ArrayList<casas>()
        var local : String? = null
        ids.clear()
        if (array != null) {

            for (todascasas : Todascasas in data){
                local=todascasas.local
                if (local.equals("Braga", ignoreCase = true)){
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


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_separadorbraga, container, false)

        val manager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (manager.phoneType == TelephonyManager.PHONE_TYPE_NONE) {
            //tablet
            val rotation = (ctxtodas?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            when (rotation) {
                Surface.ROTATION_0 -> view.rvcasas.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                Surface.ROTATION_90 -> view.rvcasas.layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
                Surface.ROTATION_180 -> view.rvcasas.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                Surface.ROTATION_270 -> {
                }
                else -> view.rvcasas.layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
            }
        } else {
            //phone
            val rotation = (ctxtodas?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            when (rotation) {
                Surface.ROTATION_0 -> view.rvcasas.layoutManager = LinearLayoutManager(activity)
                Surface.ROTATION_90 -> view.rvcasas.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                Surface.ROTATION_180 -> view.rvcasas.layoutManager = LinearLayoutManager(activity)
                Surface.ROTATION_270 -> {
                }
                else -> view.rvcasas.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            }
        }

        if (NetworkCheck.isNetworkAvailable(activity)) {

            adaptador = adaptadorrvtodas(activity)
            view.rvcasas.adapter = adaptador

            sendjsonRequest()
        } else {
            view.progress_bar.visibility = View.GONE
            view.swipe.visibility = View.VISIBLE
            adaptadoroffline = adaptadorrvtodasoffline(activity, getdados())
            view.rvcasas.adapter = adaptadoroffline
        }

        view.swipe.setColorSchemeColors(ContextCompat.getColor(activity, R.color.colorPrimaryDark), ContextCompat.getColor(activity, R.color.colorPrimaryDark), ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        view.swipe.setOnRefreshListener {
            if (NetworkCheck.isNetworkAvailable(activity)) {

                adaptador = adaptadorrvtodas(activity)
                view.rvcasas.adapter = adaptador

                sendjsonRequest()
            } else {
                adaptadoroffline = adaptadorrvtodasoffline(activity, getdados())
                view.rvcasas.adapter = adaptadoroffline
            }

            view.swipe.isRefreshing = false
        }

        view.rvcasas.addOnItemTouchListener(RecyclerViewTouchListener(activity, view.rvcasas, this))

        return view
    }

    override fun onClickListener(view: View, position: Int) {
        val casas: List<listacasas>
        casas = ids
        val cs = casas[position]
        val infocasa = Intent(activity, casaactivity::class.java)
        infocasa.putExtra("localcasa", cs.Local)
        infocasa.putExtra("precocasa", cs.Preço)
        infocasa.putExtra("imgurl", cs.IMGurl)
        infocasa.putExtra("infocs", cs.info)
        infocasa.putExtra("csid", cs.idcs)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val iv = view.findViewById<View>(R.id.imgcasa)
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(iv, "elementimg"))
            activity.startActivity(infocasa, optionsCompat.toBundle())
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        internal var ids: ArrayList<listacasas> = ArrayList()
        internal var adapter: BDAdapter? = null
        internal var ctxtodas: Context? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment separadorbraga.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): separadorbraga {
            val fragment = separadorbraga()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun getdados(): List<listasql> {

            val dados = ArrayList<listasql>()

            adapter = BDAdapter(ctxtodas!!)
            val colunas = adapter!!.numerodecolunas()
            ids.clear()


            for (i in 0 until colunas) {
                val txtexato = listasql()
                val locsqloffline = adapter!!.verlocais((i + 1).toString())
                val precsqloffline = adapter!!.verprecos((i + 1).toString())
                val infossqloffline = adapter!!.verinfos((i + 1).toString())
                val id = (i + 1).toString()
                if (locsqloffline.equals("Braga", ignoreCase = true)) {
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
}// Required empty public constructor
