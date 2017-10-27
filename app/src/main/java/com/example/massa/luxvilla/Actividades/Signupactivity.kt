package com.example.massa.luxvilla.Actividades

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.utils.firebaseutils
import com.example.massa.luxvilla.utils.mailcheck
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signupactivity.*

/**
 * Created by massa on 27/10/2017.
 */
class Signupactivity : AppCompatActivity() {


    internal var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupactivity)

        firebaseAuth = FirebaseAuth.getInstance()

        signupbutton.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(edittextusername.text.toString().trim { it <= ' ' })) {
                text_input_user_name.error = "Introduza um nome de utilizador."
                return@OnClickListener
            }

            if (!TextUtils.isEmpty(text_input_user_name.error)) {
                return@OnClickListener
            }

            if (TextUtils.isEmpty(edittextmail.text.toString().trim { it <= ' ' })) {
                text_input_mail.error = "Introduza um email."
                return@OnClickListener
            } else {
                text_input_mail.error = ""
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
            } else {
                text_input_password.error = ""
            }

            if (TextUtils.isEmpty(edittextconfirmpassword.text.toString().trim { it <= ' ' })) {
                text_input_Comfirm_password.error = "Confirme a password."
                return@OnClickListener
            } else {
                text_input_Comfirm_password.error = ""
            }

            if (!TextUtils.equals(edittextpassword.text.toString().trim { it <= ' ' },
                    edittextconfirmpassword.text.toString().trim { it <= ' ' })) {
                text_input_Comfirm_password.error = "Confirme a password."
                return@OnClickListener
            } else {
                text_input_Comfirm_password.error = ""
            }

            linearLayoutsignin.visibility = View.GONE
            linearLayoutprogressbar.visibility = View.VISIBLE

            firebaseAuth!!.createUserWithEmailAndPassword(edittextmail.text.toString().trim { it <= ' ' },
                    edittextpassword.text.toString().trim { it <= ' ' }).addOnSuccessListener {
                firebaseutils.setuserfirstdata(this@Signupactivity, edittextusername.text.toString().trim { it <= ' ' })
                startActivity(Intent(this@Signupactivity, MainActivity::class.java))
            }.addOnFailureListener {
                linearLayoutsignin.visibility = View.VISIBLE
                linearLayoutprogressbar.visibility = View.GONE
            }
        })


    }
}
