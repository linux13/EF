package com.salasamuslimah.ef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText Username, FullName, CountryName;
    private Button Save;
    private CircleImageView ProfileImage;
        private boolean connected=false;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef,postRef,myRef1,myref2,myref3;
    private StorageReference UserProfileImageRef;
    private FirebaseStorage firebaseStorage;

    private ProgressDialog LoadingBar;

    RadioGroup rdgrp;
    RadioButton male,female;

    String currentUserID;
    String username, fullname, country;
    private UploadTask uploadTask;
    private StorageReference storageReference;

    Uri imagePath;
    String chkkey,gender=null;

    final static int Gallery_Pick = 123;
        private boolean edit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);


        chkkey=getIntent().getStringExtra("key");
        if(chkkey==null)chkkey="notedit";
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        //myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        myRef = FirebaseDatabase.getInstance().getReference();
       // UserRef = FirebaseDatabase.getInstance().getReference();

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference myRef1 = storageReference.child(mAuth.getUid()).getRoot();



        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");
        LoadingBar = new ProgressDialog(this);

        InitializeFields();
        if(chkkey.equals("edit")){
            Save.setText("Update");
            loadimageandusername();
        }

        Save.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                if (validate()) {
                    boolean bl =chkInternetconnection();//network connectin chk kora or
                    if(bl==true) //true maney connection asey
                    sendUserfullData();
                    else Toast.makeText(SetUpActivity.this, "Turn on your wifi or mobile phone network then try again", Toast.LENGTH_LONG).show();
                    }


            }
        });

        rdgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);

                gender=(String) radioButton.getText().toString();
            }
        });




       /* Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Updatesettings();
              //  sendUserfullData();

            }
        });*/


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Image"), Gallery_Pick);


            }
        });


    }


    private void InitializeFields() {

        Username = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        CountryName = (EditText) findViewById(R.id.setup_country);

        Save = (Button) findViewById(R.id.save);

        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        rdgrp = findViewById(R.id.radiogrp);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        rdgrp.clearCheck();

    }

    private void Updatesettings() {
        String username = Username.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if (TextUtils.isEmpty(username)) {

            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your fullname", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country", Toast.LENGTH_SHORT).show();
        } else {
//            HashMap<String, String> profileMap = new HashMap<>();
//            profileMap.put("uid", currentUserID);
//            profileMap.put("username", username);
//            profileMap.put("fullname", fullname);
//            profileMap.put("country", country);
            //ufror comment kora eta lager nah




            myref3.push().setValue("hello")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToNavActivity();
                                Toast.makeText(SetUpActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SetUpActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }


    private void SendUserToNavActivity() {

        Intent intent = new Intent(SetUpActivity.this, NavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



      /* Save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               SaveAccountSetUpInformation();
           }
       });*/


       /* ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Image"), Gallery_Pick);

            }
        });




      /*  myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               if (dataSnapshot.exists()) {
                   if (dataSnapshot.hasChild("profileimage")) {
                       String image = dataSnapshot.child("profileimage").getValue().toString();

                       Picasso.get().load(image).placeholder(R.drawable.pro).into(ProfileImage);
                   }
               }
               else
               {
                   Toast.makeText(SetUpActivity.this, "Please select profile image..", Toast.LENGTH_SHORT).show();
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });*/


    // }
    private Boolean validate(){
        Boolean result = false;


        username = Username.getText().toString();
        fullname = FullName.getText().toString();
        country = CountryName.getText().toString();

        if (TextUtils.isEmpty(username)) {

            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your fullname", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country", Toast.LENGTH_SHORT).show();
        } else {

            result = true;
        }
        if(gender==null){
            result=false;
            Toast.makeText(SetUpActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
        }
        else result=true;
        return result;
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            CropImage.activity(imagePath)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(144, 81)
                    .start(this);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                ProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(144, 81)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {
                LoadingBar.setTitle("Profile Image");
                LoadingBar.setMessage("Please wait,while we are updating your profile image..");
                LoadingBar.show();
                LoadingBar.setCanceledOnTouchOutside(true);


                Uri resultUri = result.getUri();
                //ProfileImage.setImageURI(resultUri);

                StorageReference filepath = UserProfileImageRef.child(currentUserID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SetUpActivity.this, "Profile Image Sored Successfully to firebase storage..", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            myRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SetUpActivity.this, SetUpActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SetUpActivity.this, "Profile image stored to firebase database successfully", Toast.LENGTH_SHORT).show();
                                                LoadingBar.dismiss();

                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetUpActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                                                LoadingBar.dismiss();
                                            }

                                        }
                                    });

                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/





   private  void sendUserfullData()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef =firebaseDatabase.getReference("Users");
        StorageReference imageReference = storageReference.child(mAuth.getUid()).child("Images").child("Profile Pic");

        if(imagePath!=null) {
            UploadTask uploadTask = imageReference.putFile(imagePath);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SetUpActivity.this, "failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SetUpActivity.this, "successful!", Toast.LENGTH_SHORT).show();
                }
            });
            long s=System.currentTimeMillis();

            myRef1 =firebaseDatabase.getReference("Users");
            myref2 = myRef1.child(mAuth.getUid());


            Setupaactivityclass setupaactivityclass = new Setupaactivityclass(username,fullname,country,mAuth.getUid(),gender);
            myref2.setValue(setupaactivityclass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(!chkkey.equals("edit"))
                    Toast.makeText(SetUpActivity.this, "Your account created successfully!", Toast.LENGTH_SHORT).show();
                    else   Toast.makeText(SetUpActivity.this, "Your account updated successfully!", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetUpActivity.this, "Error occured!! Try again", Toast.LENGTH_SHORT).show();

                }
            });
            Intent intent = new Intent(SetUpActivity.this, NavActivity.class);
            intent.putExtra("login",1);
            startActivity(intent);
            finish();


        }
        else
        {
            Toast.makeText(getApplicationContext(),"Photo required",Toast.LENGTH_LONG).show();
        }


    }


    private boolean chkInternetconnection(){// if there have a connection then it will retrun true otherwise false
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

    private void loadimageandusername() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(mAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(ProfileImage);

            }
        });
        myRef1 =firebaseDatabase.getReference("Users");
        myref2 = myRef1.child(mAuth.getUid());

        myref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Setupaactivityclass set=dataSnapshot.getValue(Setupaactivityclass.class);
                if(set!=null) {
                    String s = set.getUsername();
                    String p = set.getCountry();
                    String q = set.getFullname();
                    Username.setText(s);
                    CountryName.setText(p);
                    FullName.setText(q);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SetUpActivity.this, "Error occured to load username and country", Toast.LENGTH_LONG).show();

            }
        });
    }


    }


   /* private void SaveAccountSetUpInformation ()

    {
        LoadingBar.setTitle("Saving Information");
        LoadingBar.setMessage("Please wait,while we are creating your new account..");
        LoadingBar.show();
        LoadingBar.setCanceledOnTouchOutside(true);


        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", fullname);
        userMap.put("country", country);
        userMap.put("status", "hey there!");
        userMap.put("gender", "none");
        userMap.put("dob", "none");

        myRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    // SendUserToNavActivity();
                    Toast.makeText(SetUpActivity.this, "your Account is created successfully.", Toast.LENGTH_LONG).show();
                    LoadingBar.dismiss();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SetUpActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
            }
        });
    }
    private void SendUserToNavActivity()
    {
        Intent intent = new Intent(SetUpActivity.this, NavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/


