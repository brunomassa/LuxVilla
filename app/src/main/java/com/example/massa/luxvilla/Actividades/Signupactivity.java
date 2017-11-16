package com.example.massa.luxvilla.Actividades;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
    LinearLayout layoutprogressbar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        firebaseAuth=FirebaseAuth.getInstance();

        linearLayout= findViewById(R.id.linearLayoutsignin);
        textInputLayoutusername= findViewById(R.id.text_input_user_name);
        textInputLayoutmail= findViewById(R.id.text_input_mail);
        textInputLayoutpassword= findViewById(R.id.text_input_password);
        textInputLayoutconfirmpassword= findViewById(R.id.text_input_Comfirm_password);
        username= findViewById(R.id.edittextusername);
        email= findViewById(R.id.edittextmail);
        password= findViewById(R.id.edittextpassword);
        confirmpassword= findViewById(R.id.edittextconfirmpassword);
        btnsugup= findViewById(R.id.signupbutton);

        layoutprogressbar= findViewById(R.id.linearLayoutprogressbar);

        btnsugup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(username.getText().toString().trim())){
                    textInputLayoutusername.setError("Introduza um nome de utilizador.");
                    return;
                }

                if (!TextUtils.isEmpty(textInputLayoutusername.getError())){
                    return;
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

                linearLayout.setVisibility(View.GONE);
                layoutprogressbar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),
                        password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseutils.setuserfirstdata(Signupactivity.this, username.getText().toString().trim());
                        startActivity(new Intent(Signupactivity.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        linearLayout.setVisibility(View.VISIBLE);
                        layoutprogressbar.setVisibility(View.GONE);
                    }
                });
            }
        });


    }
}
