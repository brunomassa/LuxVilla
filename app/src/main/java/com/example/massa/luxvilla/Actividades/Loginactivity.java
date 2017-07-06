package com.example.massa.luxvilla.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.massa.luxvilla.MainActivity;
import com.example.massa.luxvilla.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class Loginactivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextInputLayout textInputLayoutmail, textInputLayoutpassword;
    EditText email, password;
    AppCompatButton btnsigin,btnsugup;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        firebaseAuth=FirebaseAuth.getInstance();

        linearLayout=(LinearLayout)findViewById(R.id.linearLayoutsignin);
        textInputLayoutmail=(TextInputLayout)findViewById(R.id.text_input_mail);
        textInputLayoutpassword=(TextInputLayout) findViewById(R.id.text_input_password);
        email=(EditText)findViewById(R.id.edittextmail);
        password=(EditText)findViewById(R.id.edittextpassword);
        btnsigin=(AppCompatButton)findViewById(R.id.loginbutton);
        btnsugup=(AppCompatButton)findViewById(R.id.signupbutton);

        progressDialog=new ProgressDialog(this);

        btnsigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString().trim())){
                    textInputLayoutmail.setError("Introduza um email.");
                    return;
                }

                if (TextUtils.isEmpty(password.getText().toString().trim())){
                    textInputLayoutpassword.setError("Introduza uma password.");
                    return;
                }

                progressDialog.setMessage("A entrar...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),
                        password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Loginactivity.this,MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthException) {
                            String errorcode=((FirebaseAuthException) e).getErrorCode();

                            if (errorcode.equals("ERROR_WRONG_PASSWORD")){
                                textInputLayoutpassword.setError("Password incorreta.");
                            }

                            if (errorcode.equals("ERROR_USER_NOT_FOUND")){
                                Snackbar.make(linearLayout,"Utilizador não encontrado.", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        progressDialog.dismiss();
                    }
                });
            }
        });

        btnsugup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: signup activity
            }
        });
    }
}
