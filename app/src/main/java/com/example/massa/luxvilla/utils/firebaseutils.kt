package com.example.massa.luxvilla.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.massa.luxvilla.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.like.LikeButton
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by massa on 27/10/2017.
 */
object firebaseutils {

    fun getuserdata(context: Context, username: TextView, useremail: TextView, profileimage: CircleImageView) {
        var profileEmail: String? = ""

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            user.reload().addOnSuccessListener {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                if (user != null) {
                    for (userdata in user.providerData) {

                        val profileDisplayName = userdata.displayName
                        val profilePhotoUrl = userdata.photoUrl

                        if (profilePhotoUrl != null) {
                            val image = profilePhotoUrl.toString()
                            Picasso.with(context)
                                    .load(image)
                                    .placeholder(R.drawable.profilelogo)
                                    .fit()
                                    .into(profileimage)
                        }
                        username.text = profileDisplayName
                    }
                }
            }.addOnFailureListener { }
            profileEmail = user.email
        }

        useremail.text = profileEmail
    }

    fun setbio(bio: TextView) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance()

        if (user != null) {
            val myRef = database.getReference("users").child(user.uid).child("user_bio")

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        bio.text = dataSnapshot.getValue(String::class.java)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    fun setuserfirstdata(context: Context, username: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val builder = UserProfileChangeRequest.Builder()
        builder.setDisplayName(username)
        user?.updateProfile(builder.build())?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(context, "Ocorreu um erro", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun updateuserbio(userbio: String, linearLayout: LinearLayout) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance()

        if (user != null) {
            val myRef = database.getReference("users").child(user.uid).child("user_bio")

            myRef.setValue(userbio).addOnSuccessListener { }.addOnFailureListener { Snackbar.make(linearLayout, "Lamentamos mas ocorreu um erro", Snackbar.LENGTH_LONG).show() }
        }
    }

    fun updateusername(username: String, linearLayout: LinearLayout) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val builder = UserProfileChangeRequest.Builder()
        builder.setDisplayName(username)
        user?.updateProfile(builder.build())?.addOnSuccessListener { }?.addOnFailureListener { Snackbar.make(linearLayout, "Lamentamos mas ocorreu um erro", Snackbar.LENGTH_LONG).show() }
    }

    fun checklike(ctx: Context, id: String, likeButton: LikeButton) {
        val PREFSNAME = "FAVS"
        var uid = ""
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            uid = user.uid
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(uid).child("likes")

        myRef.orderByKey().equalTo("heart" + id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val sharedPreferences = ctx.getSharedPreferences(PREFSNAME, 0)
                    val editor: SharedPreferences.Editor
                    editor = sharedPreferences.edit()
                    editor.putInt(id, 1)
                    editor.apply()
                    likeButton.setLiked(true)
                } else {
                    val sharedPreferences = ctx.getSharedPreferences(PREFSNAME, 0)
                    val editor: SharedPreferences.Editor
                    editor = sharedPreferences.edit()
                    editor.putInt(id, 0)
                    editor.apply()
                    likeButton.setLiked(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun setlike(id: String) {
        var uid = ""
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            uid = user.uid
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(uid).child("likes")
        myRef.child("heart" + id).child("liked").setValue("true")
    }

    fun removelike(id: String) {
        var uid = ""
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            uid = user.uid
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(uid).child("likes")
        myRef.child("heart" + id).removeValue()
    }
}
