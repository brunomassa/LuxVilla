package com.example.massa.luxvilla.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massa.luxvilla.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
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

import static android.content.ContentValues.TAG;


/**
 * Created by massa on 06/07/2017.
 */

public class firebaseutils {

    public static void getuserdata(final Context context, final TextView username, TextView useremail, final CircleImageView profileimage){
        String profileEmail = "";

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();

        if (user != null){
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseAuth auth=FirebaseAuth.getInstance();
                    FirebaseUser user=auth.getCurrentUser();
                    if (user !=null){
                        for (UserInfo userdata: user.getProviderData()) {

                            String profileDisplayName=userdata.getDisplayName();
                            Uri profilePhotoUrl=userdata.getPhotoUrl();

                            if (profilePhotoUrl !=null){
                                String image=profilePhotoUrl.toString();
                                Picasso.with(context)
                                        .load(image)
                                        .placeholder(R.drawable.profilelogo)
                                        .fit()
                                        .into(profileimage);
                            }
                            username.setText(profileDisplayName);
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            profileEmail=user.getEmail();
        }

        useremail.setText(profileEmail);
    }

    public static void setbio(final TextView bio){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (user !=null){
            DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("user_bio");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        bio.setText(dataSnapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static void setuserfirstdata(final Context context, FirebaseUser user, String username){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(username);
        if (user !=null){
            user.updateProfile(builder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(context,"Ocorreu um erro",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public static void updateuserbio(String userbio, final LinearLayout linearLayout){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (user !=null) {
            DatabaseReference myRef = database.getReference("users").child(user.getUid()).child("user_bio");

            myRef.setValue(userbio).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(linearLayout,"Lamentamos mas ocorreu um erro",Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void updateusername(String username, final LinearLayout linearLayout){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(username);
        if (user !=null){
            user.updateProfile(builder.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(linearLayout,"Lamentamos mas ocorreu um erro",Snackbar.LENGTH_LONG).show();
                }
            });
        }
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
