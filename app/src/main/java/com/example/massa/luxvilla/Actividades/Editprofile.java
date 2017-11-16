package com.example.massa.luxvilla.Actividades;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class Editprofile extends AppCompatActivity {
    Toolbar toolbar;
    EditText editTextusername, editTextuserbio;
    TextInputLayout textInputLayoutusername;
    LinearLayout linearLayout;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        toolbar= findViewById(R.id.barprofileactivity);
        toolbar.setTitle("Editar perfil");
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        linearLayout= findViewById(R.id.linearLayouteditprofile);
        textInputLayoutusername= findViewById(R.id.text_input_username);

        editTextusername= findViewById(R.id.edittextusername);
        editTextuserbio= findViewById(R.id.edittextbio);

        mAuth=FirebaseAuth.getInstance();

        user=mAuth.getCurrentUser();
        if (user !=null){
            for (UserInfo userdata: user.getProviderData()) {
                String userprovider=userdata.getProviderId();

                if (userprovider.equals("google.com")){
                    textInputLayoutusername.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editprofile, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.doneedit:
                if (textInputLayoutusername.getVisibility()== View.GONE){
                    if (!TextUtils.isEmpty(editTextuserbio.getText().toString().trim())) {
                        firebaseutils.updateuserbio(editTextuserbio.getText().toString().trim(),linearLayout);
                        onBackPressed();
                    }
                    break;
                }else {
                    if (TextUtils.isEmpty(editTextusername.getText().toString().trim()) &&
                            !TextUtils.isEmpty(editTextuserbio.getText().toString().trim())){
                        firebaseutils.updateuserbio(editTextuserbio.getText().toString().trim(),linearLayout);
                        onBackPressed();
                        break;
                    }
                    if (TextUtils.isEmpty(editTextuserbio.getText().toString().trim()) &&
                            !TextUtils.isEmpty(editTextusername.getText().toString().trim())){
                        firebaseutils.updateusername(editTextusername.getText().toString().trim(),linearLayout);
                        onBackPressed();
                        break;
                    }
                }
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
