package com.example.massa.luxvilla.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massa.luxvilla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by massa on 06/07/2017.
 */

public class firebaseutils {

    public static void checkusername(String username, final TextInputLayout textInputLayout){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.orderByChild("user_name").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    textInputLayout.setError("User name já está em uso.");
                }else{
                    textInputLayout.setError("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getuserdata(Context context, final TextView username, TextView useremail, CircleImageView profileimage){
        String providerId = "";
        String profileUid = "";
        String profileDisplayName = "";
        String profileEmail = "";
        Uri profilePhotoUrl = null;
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if (user != null){
            providerId=user.getProviderId();
            profileUid=user.getUid();
            profileDisplayName=user.getDisplayName();
            profileEmail=user.getEmail();
            profilePhotoUrl=user.getPhotoUrl();
        }

        if (profilePhotoUrl !=null){
            String image=profilePhotoUrl.toString();
            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.profilelogo)
                    .resize(65, 65)
                    .centerCrop()
                    .into(profileimage);
        }

        username.setText(profileDisplayName);
        useremail.setText(profileEmail);
    }

    public static void setuserfirstdata(String userid,String username){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String,Object> data = new HashMap<>();
        data.put("user_name",username);
        data.put("user_bio","Para adicionar uma bio edite o prefil");
        myRef.child(userid).updateChildren(data);
    }

    public static void checklike(String id, final LikeButton likeButton){
        String uid="";
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if (user != null){
            uid=user.getUid();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid).child("likes");

        myRef.orderByKey().equalTo("heart"+id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    likeButton.setLiked(true);
                }else{
                    likeButton.setLiked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setlike(String id){
        String uid="";
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if (user != null){
            uid=user.getUid();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid).child("likes");
        myRef.child("heart"+id).child("liked").setValue("true");
    }

    public static void removelike(String id){
        String uid="";
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if (user != null){
            uid=user.getUid();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid).child("likes");
        myRef.child("heart"+id).removeValue();
    }
}
