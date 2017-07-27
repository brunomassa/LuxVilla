package com.example.massa.luxvilla.Actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.massa.luxvilla.R;
import com.example.massa.luxvilla.utils.mailcheck;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Loginactivity extends AppCompatActivity {

    SharedPreferences sharedPreferencesapp;
    LinearLayout linearLayout;
    TextInputLayout textInputLayoutmail, textInputLayoutpassword;
    EditText email, password;
    AppCompatButton btnsigin,btnsugup;
    LinearLayout layoutprogressbar;
    SignInButton signInButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.login);
        sharedPreferencesapp= PreferenceManager.getDefaultSharedPreferences(Loginactivity.this);
        boolean nightmode=sharedPreferencesapp.getBoolean(getResources().getString(R.string.night_mode),false);
        if (nightmode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        user=FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
            startActivity(new Intent(Loginactivity.this,MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_loginactivity);

        firebaseAuth=FirebaseAuth.getInstance();

        linearLayout=(LinearLayout)findViewById(R.id.linearLayoutsignin);
        textInputLayoutmail=(TextInputLayout)findViewById(R.id.text_input_mail);
        textInputLayoutpassword=(TextInputLayout) findViewById(R.id.text_input_password);
        email=(EditText)findViewById(R.id.edittextmail);
        password=(EditText)findViewById(R.id.edittextpassword);
        btnsigin=(AppCompatButton)findViewById(R.id.loginbutton);
        btnsugup=(AppCompatButton)findViewById(R.id.signupbutton);
        signInButton=(SignInButton)findViewById(R.id.googlesignin);

        layoutprogressbar=(LinearLayout)findViewById(R.id.linearLayoutprogressbar);

        btnsigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString().trim())){
                    textInputLayoutmail.setError("Introduza um email.");
                    return;
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
                }


                linearLayout.setVisibility(View.GONE);
                layoutprogressbar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),
                        password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(Loginactivity.this,MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        linearLayout.setVisibility(View.VISIBLE);
                        layoutprogressbar.setVisibility(View.GONE);

                        if (e instanceof FirebaseAuthException) {
                            String errorcode=((FirebaseAuthException) e).getErrorCode();

                            if (errorcode.equals("ERROR_WRONG_PASSWORD")){
                                textInputLayoutpassword.setError("Password incorreta.");
                            }

                            if (errorcode.equals("ERROR_USER_NOT_FOUND")){
                                Snackbar.make(linearLayout,"Utilizador não encontrado.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        btnsugup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Loginactivity.this,Signupactivity.class));
            }
        });

        TextView signinbuttontext=(TextView)signInButton.getChildAt(0);
        signinbuttontext.setText("Login com Google");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(linearLayout,"Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);
                layoutprogressbar.setVisibility(View.VISIBLE);

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
                layoutprogressbar.setVisibility(View.GONE);
                Snackbar.make(linearLayout,"Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Snackbar.make(linearLayout,"Ocorreu um erro ao conectar", Snackbar.LENGTH_LONG).show();
                        } else {
                            startActivity(new Intent(Loginactivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }
}
