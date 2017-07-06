package com.example.massa.luxvilla.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.massa.luxvilla.MainActivity;
import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.utils.firebaseutils;
import com.example.massa.luxvilla.utils.mailcheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signupactivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextInputLayout textInputLayoutusername, textInputLayoutmail, textInputLayoutpassword, textInputLayoutconfirmpassword;
    EditText username, email, password, confirmpassword;
    AppCompatButton btnsugup;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        firebaseAuth=FirebaseAuth.getInstance();

        linearLayout=(LinearLayout)findViewById(R.id.linearLayoutsignin);
        textInputLayoutusername=(TextInputLayout)findViewById(R.id.text_input_user_name);
        textInputLayoutmail=(TextInputLayout)findViewById(R.id.text_input_mail);
        textInputLayoutpassword=(TextInputLayout) findViewById(R.id.text_input_password);
        textInputLayoutconfirmpassword=(TextInputLayout)findViewById(R.id.text_input_Comfirm_password);
        username=(EditText)findViewById(R.id.edittextusername) ;
        email=(EditText)findViewById(R.id.edittextmail);
        password=(EditText)findViewById(R.id.edittextpassword);
        confirmpassword=(EditText)findViewById(R.id.edittextconfirmpassword);
        btnsugup=(AppCompatButton)findViewById(R.id.signupbutton);

        progressDialog=new ProgressDialog(this);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO: check if username exists
                firebaseutils.checkusername(charSequence.toString(),textInputLayoutusername);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnsugup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username.getText().toString().trim())){
                    textInputLayoutusername.setError("Introduza um nome de utilizador.");
                    return;
                }else{
                    textInputLayoutusername.setError("");
                }

                if (TextUtils.isEmpty(email.getText().toString().trim())){
                    textInputLayoutmail.setError("Introduza um email.");
                    return;
                }else{
                    textInputLayoutmail.setError("");
                }

                if (!mailcheck.isEmailValid(email.getText().toString().trim())){
                    textInputLayoutmail.setError("Indereço de email inválido.");
                    return;
                }else {
                    textInputLayoutmail.setError("");
                }

                if (TextUtils.isEmpty(password.getText().toString().trim())){
                    textInputLayoutpassword.setError("Introduza uma password.");
                    return;
                }else {
                    textInputLayoutpassword.setError("");
                }

                if (TextUtils.isEmpty(confirmpassword.getText().toString().trim())){
                    textInputLayoutconfirmpassword.setError("Confirme a password.");
                    return;
                }else {
                    textInputLayoutconfirmpassword.setError("");
                }

                if (!TextUtils.equals(password.getText().toString().trim(),
                        confirmpassword.getText().toString().trim())){
                    textInputLayoutconfirmpassword.setError("Confirme a password.");
                    return;
                }else {
                    textInputLayoutconfirmpassword.setError("");
                }

                progressDialog.setMessage("A criar utilizador...");
                progressDialog.show();
                Snackbar.make(linearLayout,"OK",Snackbar.LENGTH_LONG).show();

                /*firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),
                        password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Signupactivity.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/
            }
        });


    }
}
