package com.salasamuslimah.ef;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ImageButton AddNewPostButton;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;

    private FirebaseAuth mAuth;

    private DatabaseReference myRef;

    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        myRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

         AddNewPostButton = (ImageButton)findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(NavActivity.this, drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView)navView.findViewById(R.id.nav_user_full_name);

        myRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists())
               {
                   if (dataSnapshot.hasChild("fullname"))
                   {
                       String fullname = dataSnapshot.child("fullname").getValue().toString();
                       NavProfileUserName.setText(fullname);
                   }
                   if(dataSnapshot.hasChild("profileimage"))
                   {
                       String image = dataSnapshot.child("profileimage").getValue().toString();


                       Picasso.get().load(image).placeholder(R.drawable.pro).into(NavProfileImage);


                   }
                   else
                   {
                       Toast.makeText(NavActivity.this, "Profile name do not exists..", Toast.LENGTH_SHORT).show();
                   }


               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelecter(item);
                return false;
            }

        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                SendUserToPostActivity();

            }
        });



    }

    private void SendUserToPostActivity()
    {
        Intent addNewPOstIntent = new Intent(NavActivity.this, PostActivity.class);
        startActivity(addNewPOstIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence()
    {
      final String current_user_id = mAuth.getCurrentUser().getUid();

      myRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              if (!dataSnapshot.hasChild(current_user_id))
              {
                // SendUserToSetUpActivity();
              }
          }

          @Override
          public void onCancelled(DatabaseError error) {

          }
      });
    }

   /*private void SendUserToSetUpActivity()
    {
        Intent setupnewIntent = new Intent(NavActivity.this,SetUpActivity.class);
        setupnewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupnewIntent);
        finish();

    }*/

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(NavActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelecter(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_events:
               SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_shared:
                Toast.makeText(this, "shared", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_create:
                Toast.makeText(this, "create", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_edit:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_inbox:
                Toast.makeText(this, "inbox", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_pages:
                Toast.makeText(this, "pages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_planner:
                Toast.makeText(this, "planner", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

        }
    }

}
