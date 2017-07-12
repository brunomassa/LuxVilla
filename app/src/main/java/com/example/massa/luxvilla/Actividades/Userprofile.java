package com.example.massa.luxvilla.Actividades;

import android.net.Uri;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.separadores.separadorlikes;
import com.example.massa.luxvilla.separadores.separadorsobre;
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
    ViewPager vwpgr;
    TabLayout tbs;
    final int SEPARADOR_SOBRE=0,SEPARADOR_LIKES=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        toolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.tbprofile);
        toolbar=(Toolbar)findViewById(R.id.barprofileactivity);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
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
                profileimage.setImageDrawable(ContextCompat.getDrawable(Userprofile.this,R.drawable.nouserimage));
            }
        }
        tbs=(TabLayout)findViewById(R.id.profiletabs);
        tbs.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        vwpgr=(ViewPager)findViewById(R.id.profilevpgr);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private class adaptadortabs extends FragmentPagerAdapter{

        public adaptadortabs(FragmentManager fm) {
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
