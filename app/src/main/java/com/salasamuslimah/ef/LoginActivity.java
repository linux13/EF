package com.salasamuslimah.ef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login,register;
    private ProgressDialog loadingBar;


    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = mAuth.getCurrentUser();
            }
        };


        email = (EditText) findViewById(R.id.login_email);
        password =(EditText) findViewById(R.id.login_password);
        login =(Button) findViewById(R.id.loginid);
        register =(Button) findViewById(R.id.registerid);
        loadingBar = new ProgressDialog(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                userlogin();


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent (LoginActivity.this,RegisterActivity.class);

                startActivity(intent);

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            SendUserToNavActivity();  //if the user is already login,send him/her to nav activity
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null)
        {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void SendUserToNavActivity(){

        Intent intent = new Intent (LoginActivity.this,NavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void userlogin(){



        String emailaddress = email.getText().toString().trim();
        String passwordcode = password.getText().toString().trim();



        if (emailaddress.isEmpty()){
            email.setError("email is required");
            email.requestFocus();
            return;
        }


        if (passwordcode.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }


        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait..");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);


        mAuth.signInWithEmailAndPassword(emailaddress , passwordcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!(task.isComplete()))
                {
                    Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                }

                if (task.isSuccessful()){

                    SendUserToNavActivity();

                    loadingBar.dismiss();

                    Toast.makeText(LoginActivity.this, "You are Logged in Successfully", Toast.LENGTH_SHORT).show();

                }else
                    {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}

