package com.example.massa.luxvilla.Actividades

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.utils.firebaseutils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_editprofile.*

/**
 * Created by massa on 27/10/2017.
 */
class Editprofile : AppCompatActivity() {
    internal var mAuth: FirebaseAuth? = null
    internal var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        barprofileactivity.title = "Editar perfil"
        barprofileactivity.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp)
        setSupportActionBar(barprofileactivity)


        mAuth = FirebaseAuth.getInstance()

        user = mAuth!!.currentUser
        if (user != null) {
            for (userdata in user!!.providerData) {
                val userprovider = userdata.providerId

                if (userprovider == "google.com") {
                    text_input_username.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_editprofile, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.doneedit -> {
                if (text_input_username.visibility == View.GONE) {
                    if (!TextUtils.isEmpty(edittextbio.text.toString().trim { it <= ' ' })) {
                        firebaseutils.updateuserbio(edittextbio.text.toString().trim { it <= ' ' }, linearLayouteditprofile)
                        onBackPressed()
                    }
                    return false
                } else {
                    if (TextUtils.isEmpty(edittextusername.text.toString().trim { it <= ' ' }) && !TextUtils.isEmpty(edittextbio.text.toString().trim { it <= ' ' })) {
                        firebaseutils.updateuserbio(edittextbio.text.toString().trim { it <= ' ' }, linearLayouteditprofile)
                        onBackPressed()
                        return false
                    }
                    if (TextUtils.isEmpty(edittextbio.text.toString().trim { it <= ' ' }) && !TextUtils.isEmpty(edittextusername.text.toString().trim { it <= ' ' })) {
                        firebaseutils.updateusername(edittextusername.text.toString().trim { it <= ' ' }, linearLayouteditprofile)
                        onBackPressed()
                        return false
                    }
                }
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
