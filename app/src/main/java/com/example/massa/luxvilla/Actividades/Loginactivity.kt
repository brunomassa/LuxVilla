package com.example.massa.luxvilla.Actividades

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.utils.mailcheck
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_loginactivity.*

/**
 * Created by massa on 27/10/2017.
 */
class Loginactivity : AppCompatActivity() {

    internal var sharedPreferencesapp: SharedPreferences? = null

    internal var firebaseAuth: FirebaseAuth? = null
    internal var user: FirebaseUser? = null

    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.login)
        sharedPreferencesapp = PreferenceManager.getDefaultSharedPreferences(this@Loginactivity)
        val nightmode = sharedPreferencesapp!!.getBoolean(resources.getString(R.string.night_mode), false)
        if (nightmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)

        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this@Loginactivity, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_loginactivity)

        firebaseAuth = FirebaseAuth.getInstance()


        loginbutton.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(edittextmail.text.toString().trim { it <= ' ' })) {
                text_input_mail.error = "Introduza um email."
                return@OnClickListener
            }

            if (!mailcheck.isEmailValid(edittextmail.text.toString().trim { it <= ' ' })) {
                text_input_mail.error = "Indereço de email inválido."
                return@OnClickListener
            } else {
                text_input_mail.error = ""
            }

            if (TextUtils.isEmpty(edittextpassword.text.toString().trim { it <= ' ' })) {
                text_input_password.error = "Introduza uma password."
                return@OnClickListener
            }


            linearLayoutsignin.visibility = View.GONE
            linearLayoutprogressbar.visibility = View.VISIBLE

            firebaseAuth!!.signInWithEmailAndPassword(edittextmail.text.toString().trim { it <= ' ' },
                    edittextpassword.text.toString().trim { it <= ' ' }).addOnSuccessListener {
                startActivity(Intent(this@Loginactivity, MainActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                linearLayoutsignin.visibility = View.VISIBLE
                linearLayoutprogressbar.visibility = View.GONE

                if (e is FirebaseAuthException) {
                    val errorcode = e.errorCode

                    if (errorcode == "ERROR_WRONG_PASSWORD") {
                        text_input_password.error = "Password incorreta."
                    }

                    if (errorcode == "ERROR_USER_NOT_FOUND") {
                        Snackbar.make(linearLayoutsignin, "Utilizador não encontrado.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

        signupbutton.setOnClickListener { startActivity(Intent(this@Loginactivity, Signupactivity::class.java)) }

        val signinbuttontext = googlesignin.getChildAt(0) as TextView
        signinbuttontext.text = "Login com Google"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { Snackbar.make(linearLayoutsignin, "Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show() }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        googlesignin.setOnClickListener {
            linearLayoutsignin.visibility = View.GONE
            linearLayoutprogressbar.visibility = View.VISIBLE

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                linearLayoutsignin.visibility = View.VISIBLE
                linearLayoutprogressbar.visibility = View.GONE
                Snackbar.make(linearLayoutsignin, "Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful) {
                        Snackbar.make(linearLayoutsignin, "Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show()
                    } else {
                        startActivity(Intent(this@Loginactivity, MainActivity::class.java))
                        finish()
                    }
                }
    }

    companion object {
        private val RC_SIGN_IN = 9001
    }
}
