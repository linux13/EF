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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText Username , FullName , CountryName;
    private Button Save;
    private CircleImageView ProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private StorageReference UserProfileImageRef;

    private ProgressDialog LoadingBar;

    String currentUserID;
    private UploadTask uploadTask;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");
        LoadingBar = new ProgressDialog(this);

        Username = (EditText) findViewById(R.id.setup_username);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        CountryName = (EditText) findViewById(R.id.setup_country);
        Save = (Button) findViewById(R.id.save);

        ProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveAccountSetUpInformation();

            }
        });

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profilleimage").getValue().toString();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && requestCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                LoadingBar.setTitle("Profile Image");
                LoadingBar.setMessage("Please wait,while we are updating your profile image..");
                LoadingBar.show();
                LoadingBar.setCanceledOnTouchOutside(true);


                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(SetUpActivity.this, "Profile Image Sored Successfully to firebase storage..", Toast.LENGTH_SHORT).show();

                                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            final String downloadUrl = uri.toString();

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
                                    });

                                }

                            }

                        });
                }

              else
            {

                Toast.makeText(SetUpActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                LoadingBar.dismiss();
            }
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
