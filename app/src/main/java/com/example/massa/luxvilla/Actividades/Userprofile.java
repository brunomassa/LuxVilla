package com.example.massa.luxvilla.Actividades;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.massa.luxvilla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class Userprofile extends AppCompatActivity {
    ImageView profileimage;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        profileimage=(ImageView)findViewById(R.id.imgprofile);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if (user !=null) {
            Uri userphoto = user.getPhotoUrl();
            if (userphoto !=null){
                String image=userphoto.toString();
                Picasso.with(Userprofile.this)
                        .load(image)
                        .fit()
                        .into(profileimage);
            }else{
                profileimage.setImageDrawable(ContextCompat.getDrawable(Userprofile.this,R.drawable.logo));
            }
        }
    }
}
