package com.salasamuslimah.ef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText Username , FullName , CountryName;
    private Button Save;
    private CircleImageView ProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private ProgressDialog LoadingBar;

    String currentUserID;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        LoadingBar = new ProgressDialog(this);

        Username = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        CountryName = (EditText) findViewById(R.id.setup_country);
        Save = (Button) findViewById(R.id.save);

        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               Intent galleryIntent = new Intent();
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("image/*");
               startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveAccountSetUpInformation();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && requestCode==RESULT_OK && dara!=null)
        {
            Uri ImageUri = data.getData();
        }
    }

    private void SaveAccountSetUpInformation()
    {

        String username = Username.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Please write your fullname", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(country))
        {
            Toast.makeText(this, "Please write your country", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Saving Information");
            LoadingBar.setMessage("Please wait,while we are creating your new account..");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("country",country);
            userMap.put("status","hey there!");
            userMap.put("gender","none");
            userMap.put("dob","none");

            myRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {

                 if(task.isSuccessful())
                 {
                     SendUserToNavActivity();
                     Toast.makeText(SetUpActivity.this, "your Account is created successfully.", Toast.LENGTH_LONG).show();
                     LoadingBar.dismiss();
                 }
                 else
                 {
                     String message = task.getException().getMessage();
                     Toast.makeText(SetUpActivity.this, "Error Occured"+ message, Toast.LENGTH_SHORT).show();
                     LoadingBar.dismiss();
                 }
                }
            });
        }

    }

    private void SendUserToNavActivity()
    {
        Intent intent = new Intent (SetUpActivity.this,NavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
