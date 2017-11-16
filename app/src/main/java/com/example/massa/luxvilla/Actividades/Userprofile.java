package com.example.massa.luxvilla.Actividades;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.separadores.separadorlikes;
import com.example.massa.luxvilla.separadores.separadorsobre;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Userprofile extends AppCompatActivity {
    CircleImageView profileimage;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String profileDisplayName;
    Uri profilePhotoUrl;
    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;
    ViewPager vwpgr;
    TabLayout tbs;
    final int SEPARADOR_SOBRE=0,SEPARADOR_LIKES=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        toolbarLayout= findViewById(R.id.tbprofile);
        toolbar= findViewById(R.id.barprofileactivity);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        profileimage= findViewById(R.id.imgprofile);
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
                profileimage.setImageDrawable(ContextCompat.getDrawable(Userprofile.this,R.drawable.nouserimage));
            }
        }
        tbs= findViewById(R.id.profiletabs);
        tbs.setSelectedTabIndicatorColor(ContextCompat.getColor(Userprofile.this,R.color.colorAccent));
        vwpgr= findViewById(R.id.profilevpgr);
        adaptadortabs adaptador=new adaptadortabs(getSupportFragmentManager());
        vwpgr.setAdapter(adaptador);
        tbs.setupWithViewPager(vwpgr);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.editprofile:
                startActivity(new Intent(Userprofile.this,Editprofile.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onResume(){
        super.onResume();

        if (user !=null){
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseAuth auth=FirebaseAuth.getInstance();
                    FirebaseUser user=auth.getCurrentUser();
                    if (user !=null){
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
                            profileimage.setImageDrawable(ContextCompat.getDrawable(Userprofile.this,R.drawable.nouserimage));
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private class adaptadortabs extends FragmentPagerAdapter{

        adaptadortabs(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case SEPARADOR_SOBRE:
                    return new separadorsobre();
                case SEPARADOR_LIKES:
                    return new separadorlikes();
            }
            return null;
        }

        public CharSequence getPageTitle(int position){
            String[] tabsname={"SOBRE","LIKES"};

            return tabsname[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
