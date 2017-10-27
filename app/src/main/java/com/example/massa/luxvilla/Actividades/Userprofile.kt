package com.example.massa.luxvilla.Actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.separadores.separadorlikes
import com.example.massa.luxvilla.separadores.separadorsobre
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_userprofile.*

/**
 * Created by massa on 27/10/2017.
 */
class Userprofile : AppCompatActivity() {
    internal var mAuth: FirebaseAuth? = null
    internal var user: FirebaseUser? = null
    internal var profileDisplayName: String? = null
    internal var profilePhotoUrl: Uri? = null
    internal val SEPARADOR_SOBRE = 0
    internal val SEPARADOR_LIKES = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)

        barprofileactivity.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp)
        setSupportActionBar(barprofileactivity)
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        if (user != null) {
            for (userdata in user!!.providerData) {

                profileDisplayName = userdata.displayName
                tbprofile.title = profileDisplayName
                profilePhotoUrl = userdata.photoUrl
            }
            if (profilePhotoUrl != null) {
                val image = profilePhotoUrl!!.toString()
                Picasso.with(this@Userprofile)
                        .load(image)
                        .fit()
                        .into(imgprofile)
            } else {
                imgprofile.setImageDrawable(ContextCompat.getDrawable(this@Userprofile, R.drawable.nouserimage))
            }
        }
        profiletabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this@Userprofile, R.color.colorAccent))
        val adaptador = adaptadortabs(supportFragmentManager)
        profilevpgr.adapter = adaptador
        profiletabs.setupWithViewPager(profilevpgr)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_profile, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.editprofile -> startActivity(Intent(this@Userprofile, Editprofile::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onResume() {
        super.onResume()

        if (user != null) {
            user!!.reload().addOnSuccessListener {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                if (user != null) {
                    for (userdata in user.providerData) {
                        profileDisplayName = userdata.displayName
                        tbprofile.title = profileDisplayName
                        profilePhotoUrl = userdata.photoUrl

                    }
                    if (profilePhotoUrl != null) {
                        val image = profilePhotoUrl!!.toString()
                        Picasso.with(this@Userprofile)
                                .load(image)
                                .fit()
                                .into(imgprofile)
                    } else {
                        imgprofile.setImageDrawable(ContextCompat.getDrawable(this@Userprofile, R.drawable.nouserimage))
                    }
                }
            }.addOnFailureListener { }
        }
    }

    private inner class adaptadortabs internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                SEPARADOR_SOBRE -> return separadorsobre()
                SEPARADOR_LIKES -> return separadorlikes()
            }
            return null
        }

        override fun getPageTitle(position: Int): CharSequence {
            val tabsname = arrayOf("SOBRE", "LIKES")

            return tabsname[position]
        }

        override fun getCount(): Int {
            return 2
        }
    }
}
