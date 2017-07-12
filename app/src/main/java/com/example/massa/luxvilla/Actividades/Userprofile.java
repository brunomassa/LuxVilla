package com.example.massa.luxvilla.Actividades;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.massa.luxvilla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class Userprofile extends AppCompatActivity {
    ImageView profileimage;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String profileDisplayName;
    Uri profilePhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        profileimage=(ImageView)findViewById(R.id.imgprofile);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if (user !=null) {
            for (UserInfo userdata: user.getProviderData()) {

                profileDisplayName=userdata.getDisplayName();
                profilePhotoUrl=userdata.getPhotoUrl();
            }
            if (profilePhotoUrl !=null){
                String image=profilePhotoUrl.toString();
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
