package com.salasamuslimah.ef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.salasamuslimah.ef.NavActivity.arr;
import static com.salasamuslimah.ef.NavActivity.eventList;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login,register,efusers;
    private ProgressDialog loadingBar;
        private  boolean connected=false;

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;

    String auth="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        int y=getIntent().getIntExtra("nav",-1);
        auth=getIntent().getStringExtra("auth");
        if(y!=-1)
        loadextraactivis();
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
        efusers=findViewById(R.id.efusers2);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            boolean bl=chkInternetconnection();
            if(bl==true)
                userlogin();

            else Toast.makeText(LoginActivity.this, "Turn on your wifi or mobile phone network for login", Toast.LENGTH_LONG).show();


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent (LoginActivity.this,RegisterActivity.class);

                startActivity(intent);

            }
        });
        efusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chkInternetconnection()){
                    Intent intent=new Intent(LoginActivity.this,Ef_users.class);
                    intent.putExtra("login",1);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Internet connection is needed", Toast.LENGTH_SHORT).show();
                }
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
        intent.putExtra("login",1);
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


    private boolean chkInternetconnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }


    private void loadextraactivis() {
        for(int i=0;i<157;i++){
            if(arr[i]==1||arr[i]==2){
                setfirebase(i,auth);
                arr[i]=-1;
            }
        }
    }


    private void setfirebase(int i,String auth) {
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rf1, rf2, rf4;
        rf1 = firebaseDatabase.getReference("Events");
        rf2 = rf1.child(""+auth);
        rf4 = rf2.child("" + eventList.get(i).getEventid());
        Eventprofile ev=eventList.get(i);
        rf4.setValue(ev).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }
}

