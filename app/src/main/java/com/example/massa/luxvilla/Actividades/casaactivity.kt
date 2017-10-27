package com.example.massa.luxvilla.Actividades

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.Slide
import android.transition.TransitionManager
import android.view.View
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.network.VolleySingleton
import com.example.massa.luxvilla.utils.firebaseutils
import com.like.IconType
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_casaactivity.*

/**
 * Created by massa on 27/10/2017.
 */
class casaactivity : AppCompatActivity() {

    internal var localcasa: String = ""
    internal var precocasa: String = ""
    internal var imgurlcasa: String = ""
    internal var infocasa: String = ""
    internal var idcasa: String = ""
    internal var sharedPreferences: SharedPreferences? = null
    internal var isappopen: SharedPreferences? = null
    internal var editor: SharedPreferences.Editor? = null
    internal var PREFSNAME = "FAVS"
    internal val ISOPENAPP = "appstate"
    internal var favflag: Int = 0
    internal var nightModeFlags: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tr = ChangeBounds()
            window.sharedElementEnterTransition = tr
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_casaactivity)

        nightModeFlags = this@casaactivity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        isappopen = getSharedPreferences(ISOPENAPP, 0)
        editor = isappopen!!.edit()
        editor!!.putInt("open", 1)
        editor!!.apply()

        localcasa = intent.getStringExtra("localcasa")
        precocasa = intent.getStringExtra("precocasa")
        imgurlcasa = intent.getStringExtra("imgurl")
        infocasa = intent.getStringExtra("infocs")
        idcasa = intent.getStringExtra("csid")

        barinfocasaactivity.title = localcasa
        if (isNetworkAvailable(this@casaactivity)) {
            barinfocasaactivity.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp)
        } else {
            barinfocasaactivity.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp)
        }

        barinfocasaactivity.setNavigationOnClickListener { onBackPressed() }

        if (isNetworkAvailable(this@casaactivity)) {

            Picasso.with(this@casaactivity).load(imgurlcasa).error(R.drawable.logo).into(imginfocasaactivity)

        } else {
            imginfocasaactivity.setImageResource(R.drawable.logo)
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    tbinfocasaactivity.setExpandedTitleColor(ContextCompat.getColor(this@casaactivity, android.R.color.black))
                    tbinfocasaactivity.setCollapsedTitleTextColor(ContextCompat.getColor(this@casaactivity, android.R.color.white))
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    tbinfocasaactivity.setExpandedTitleColor(ContextCompat.getColor(this@casaactivity, android.R.color.black))
                    tbinfocasaactivity.setCollapsedTitleTextColor(ContextCompat.getColor(this@casaactivity, android.R.color.black))
                }
            }
        }


        favbuttoncasa.setIcon(IconType.Heart)
        favbuttoncasa.setIconSizeDp(25)
        favbuttoncasa.setCircleEndColorRes(R.color.colorAccent)
        favbuttoncasa.setExplodingDotColorsRes(R.color.colorPrimary, R.color.colorAccent)
        favbuttoncasa.setLikeDrawableRes(R.drawable.heartliked)
        favbuttoncasa.setUnlikeDrawableRes(R.drawable.heartunliked)

        sharedPreferences = getSharedPreferences(PREFSNAME, 0)
        favflag = sharedPreferences!!.getInt(idcasa, 0)

        firebaseutils.checklike(this@casaactivity, idcasa, favbuttoncasa)


        favbuttoncasa.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                favflag = sharedPreferences!!.getInt(idcasa, 0)

                sharedPreferences = getSharedPreferences(PREFSNAME, 0)
                editor = sharedPreferences!!.edit()
                editor!!.putInt(idcasa, 1)
                editor!!.apply()
                favflag = sharedPreferences!!.getInt(idcasa, 0)

                firebaseutils.setlike(idcasa)

                favbuttoncasa.setLiked(true)
            }

            override fun unLiked(likeButton: LikeButton) {

                favflag = sharedPreferences!!.getInt(idcasa, 0)

                sharedPreferences = getSharedPreferences(PREFSNAME, 0)
                editor = sharedPreferences!!.edit()
                editor!!.putInt(idcasa.toString(), 0)
                editor!!.apply()
                favflag = sharedPreferences!!.getInt(idcasa.toString(), 0)

                firebaseutils.removelike(idcasa)

                favbuttoncasa.setLiked(false)
            }
        })


        txtinfocasaactivity.text = "Preço: " + precocasa + "\n\n" + infocasa

    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(card_viewinfo, Slide())
            card_viewinfo.visibility = View.INVISIBLE
        }
        if (isTaskRoot) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            super.onBackPressed()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
