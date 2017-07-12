package com.example.massa.luxvilla.Actividades;

import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.massa.luxvilla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class Userprofile extends AppCompatActivity {
    CircleImageView profileimage;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String profileDisplayName;
    Uri profilePhotoUrl;
    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        toolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.tbprofile);
        toolbar=(Toolbar)findViewById(R.id.barprofileactivity);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        profileimage=(CircleImageView) findViewById(R.id.imgprofile);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if (user !=null) {
            for (UserInfo userdata: user.getProviderData()) {

                profileDisplayName=userdata.getDisplayName();
                toolbarLayout.setTitle(profileDisplayName);
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
