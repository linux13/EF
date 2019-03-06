package com.salasamuslimah.ef;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavActivity extends AppCompatActivity {
        int ii=0;
    ListView listView;

    String mm="";

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ImageButton AddNewPostButton;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ListView list_view;
    private View eventFragmentView;
    private Context context;
    private RecyclerView AllEventsActivity;

    private FirebaseAuth mAuth;
    public static boolean chkDelete = false;
    private DatabaseReference myRef, UserRef, postRef, myref1, myref2;
    public static int size = 0;

    String currentUserID;
    private FirebaseStorage firebaseStorage;

    static List<Eventprofile> eventList;

    private boolean connected = false;

    ListView list;
    CustomAdapter customAdapter;

    static int []arr = new int[160];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Arrays.fill(arr,-1);
        //loadarray();
        //loadextraactivis();
        mAuth = FirebaseAuth.getInstance();
        int y = getIntent().getIntExtra("chk", -1);
        chkDelete = false;
        int chklogin = getIntent().getIntExtra("login", -1);
        if (chklogin == 1) eventList = null;
        int chkedittakiisay = getIntent().getIntExtra("editoialisay", -1);
        list = findViewById(R.id.listview);
        eventList = new ArrayList<>();
        if (chkInternetconnection()) {
            loadimageandusername();
            loadEventlist();

        } else {
            showDialoguforinternetconnection();
        }

//        if (eventList == null) {//all data first glance ou load korar lagia
//            //Toast.makeText(NavActivity.this, "Null faisay eventlist", Toast.LENGTH_SHORT).show();
//            eventList = new ArrayList<>();
//            if (chkInternetconnection()) {
//                loadimageandusername();
//                loadEventlist();
//
//                //Toast.makeText(NavActivity.this, "sizee->"+eventList.size(), Toast.LENGTH_SHORT).show();
//
//
//            } else {
//                showDialoguforinternetconnection();
//            }
//        } else {
//            if (chkedittakiisay == 1) {
//                if (chkInternetconnection()) {
//                    //Toast.makeText(NavActivity.this, "editoialisay", Toast.LENGTH_SHORT).show();
//                    eventList = new ArrayList<>();
//                    loadimageandusername();
//                    loadEventlist();
//                } else {
//                    showDialoguforinternetconnection();
//                }
//
//            } else {
////                eventList=new ArrayList<>();
////                loadimageandusername();
//
//                if (chkInternetconnection()) {
//                    eventList = new ArrayList<>();
//                    loadimageandusername();
//                    loadEventlist();
//
//                } else {
//                    showDialoguforinternetconnection();
//                }
//            }
//
//        }
        // Toast.makeText(NavActivity.this, "size "+eventList.size(), Toast.LENGTH_SHORT).show();


        mToolbar = (Toolbar) findViewById(R.id.main_page_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(NavActivity.this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        myRef = FirebaseDatabase.getInstance().getReference();
        // UserRef = FirebaseDatabase.getInstance().getReference();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(mAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(NavProfileImage);

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showActionDialog(i);//dialog show korbo
            }
        });
//
//        myRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//               if (dataSnapshot.exists())
//               {
//                   if (dataSnapshot.hasChild("fullname"))
//                   {
//                       String fullname = dataSnapshot.child("fullname").getValue().toString();
//                       NavProfileUserName.setText(fullname);
//                   }
//                   if(dataSnapshot.hasChild("profileimage"))
//                   {
//                       String image = dataSnapshot.child("profileimage").getValue().toString();
//
//
//                       Picasso.get().load(image).placeholder(R.drawable.pro).into(NavProfileImage);
//
//
//                   }
//                   else
//                   {
//                      // Toast.makeText(NavActivity.this, "Profile name do not exists..", Toast.LENGTH_SHORT).show();
//                   }
//
//
//               }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelecter(item);
                return false;
            }

        });


        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(NavActivity.this, CreateEvent.class));
            }
        });


    }

    private void showDialoguforinternetconnection() {

        final AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_LIGHT);

        alertDialogBuidler.setCancelable(false);
        alertDialogBuidler.setMessage("Internet connection is required to load all data.\nSo please turn on your wifi or mobile data first then press ok");
        alertDialogBuidler.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (chkInternetconnection()) {
                    //Toast.makeText(context, "oisay", Toast.LENGTH_SHORT).show();
                    //dialogInterface.cancel();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuidler.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkInternetconnection()) {
                    loadimageandusername();
                    loadEventlist();

                    alertDialog.dismiss();
                }
            }
        });

    }

    private void loadimageandusername() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(mAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(NavProfileImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NavActivity.this, "Image coudn't loaded", Toast.LENGTH_SHORT).show();
            }
        });

        myref1 = firebaseDatabase.getReference("Users");
        myref2 = myref1.child(mAuth.getUid());

        myref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Setupaactivityclass set = dataSnapshot.getValue(Setupaactivityclass.class);
                if (set != null) {
                    String s = set.getUsername();
                    NavProfileUserName.setText(s);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NavActivity.this, "Error occured to load username", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loadEventlist() {
        eventList=new ArrayList<>();
        eventList.clear();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef1, myref2;
        mAuth = FirebaseAuth.getInstance();
        myRef1 = firebaseDatabase.getReference("Events");
        myref2 = myRef1.child(mAuth.getUid());
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thread();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // Log.e("data",""+data.getKey());
                    getInformation(data.getKey()); //per id'r maney per event or lagia list neoa or;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NavActivity.this, "Error occured while loading data", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getInformation(String id) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef1, myref2, myref3;
        myRef1 = firebaseDatabase.getReference("Events");
        myref2 = myRef1.child(mAuth.getUid());
        myref3 = myref2.child(id);
        myref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Eventprofile eventprofile = dataSnapshot.getValue(Eventprofile.class);
                if(eventList!=null){
                    eventList.add(eventprofile);
                }

//               Log.e("data",""+eventList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NavActivity.this, "Error occured while loading data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void thread() {
        Toast.makeText(NavActivity.this, "Please wait data loading process is running\nIt will take few moments", Toast.LENGTH_SHORT).show();
        Thread t = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadeventllist2();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }

    private void loadeventllist2() {
        if (eventList.size() != 0) {
            customAdapter = new CustomAdapter(NavActivity.this, eventList);
            list.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();
        } else
            Toast.makeText(NavActivity.this, "There is " + eventList.size() + " events", Toast.LENGTH_SHORT).show();
    }


    private void SendUserToPostActivity() {
        Intent addNewPOstIntent = new Intent(NavActivity.this, PostActivity.class);
        startActivity(addNewPOstIntent);
    }

    private void SendUserToCreateEvent() {
        Intent createIntent = new Intent(NavActivity.this, CreateEvent.class);
        startActivity(createIntent);
    }


    private void SendUserToMyProfileActivity() {
        Intent profileIntent = new Intent(NavActivity.this, MyProfileActivity.class);
        startActivity(profileIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)

        {
            SendUserToLoginActivity();
        } else

        {
            CheckUserExistence();
        }

    }


    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
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


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(NavActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginIntent.putExtra("nav",1);
        loginIntent.putExtra("auth",""+mm);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToAllEventActivity() {
        Intent allEventIntent = new Intent(NavActivity.this, All_Events.class);
        allEventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(allEventIntent);
        finish();
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(NavActivity.this, Activity_Profile.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    private void SendUserToEventActivity() {
        Intent eventIntent = new Intent(NavActivity.this, NewEventActivity.class);
        eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(eventIntent);
        finish();
    }

    private void SendUserToSetUpActivity() {
        Intent eventIntent = new Intent(NavActivity.this, SetUpActivity.class);
        eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(eventIntent);
        finish();
    }

    private void SendUserToFindActivity() {
        Intent eventIntent = new Intent(NavActivity.this, FindEventsActivity.class);
        eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(eventIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelecter(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                Intent intent = new Intent(NavActivity.this, MyProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.Ef_users:
                if (chkInternetconnection()) {
                    intent = new Intent(NavActivity.this, Ef_users.class);
                    startActivity(intent);
                } else
                    Toast.makeText(NavActivity.this, "Need internet connection", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_shared:
                // SendUserToFindActivity();
                break;

            case R.id.nav_create:
                SendUserToCreateEvent();
                break;


            case R.id.nav_inbox:
                Toast.makeText(this, "inbox", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_pages:
                SendUserToProfileActivity();
                break;

            case R.id.nav_planner:
                Toast.makeText(this, "planner", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
               mm=mAuth.getUid();
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

        }
    }

    private boolean chkInternetconnection() {// if there have a connection then it will retrun true otherwise false
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        return connected;
    }


    private void showActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Show Event", "Edit", "Delete"};

        AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this);

        alertDialogBuidler.setTitle("Choose Option")
                .setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //show event or lagi
                            Intent intent = new Intent(NavActivity.this, NewEventActivity.class);
                            intent.putExtra("id", position);
                            startActivity(intent);

                        } else if (which == 1) {
                            //edit or lagia
                            Intent intent = new Intent(NavActivity.this, CreateEvent.class);
                            intent.putExtra("id", position);
                            startActivity(intent);

                        } else {
                            //delete or lagi
                            if (chkInternetconnection()){
                                shwconfirmdialogue(position);
                                deletepublicevents(position);
                            }
                            else
                                Toast.makeText(NavActivity.this, "Need Internet connection to delete an event", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .show();

    }

    private void shwconfirmdialogue(final int position) {
        AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(NavActivity.this);
        alertDialogBuidler.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        alertDialogBuidler.setMessage("Are you sure to delete?");
        alertDialogBuidler.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (chkInternetconnection()) {
                    chkDelete = true;//ekta kunu khamor nay
                    String id = eventList.get(position).getEventid();
                    //eventList.remove(eventList.get(position));
                    deleteEventfromfirebase(id);
                    Intent intent = new Intent(NavActivity.this, Delete_event.class);
                    //intent.putExtra("pos",position);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//                    ListView listView = findViewById(R.id.listview);
//                    customAdapter = new CustomAdapter(NavActivity.this, eventList);
//                    listView.setAdapter(customAdapter);
                    dialogInterface.cancel();


                } else {
                    Toast.makeText(NavActivity.this, "Internet connection is required to delete an event", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialogBuidler.show();

    }

    private void deleteEventfromfirebase(String id) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef1, myref3;
        mAuth = FirebaseAuth.getInstance();

        myRef1 = firebaseDatabase.getReference("Events");
        myref2 = myRef1.child(mAuth.getUid());
        myref3 = myref2.child("" + id);
        myref3.removeValue();
        myRef1 = firebaseDatabase.getReference("PublicEvents");
        myref2 = myRef1.child(mAuth.getUid());
        myref3 = myref2.child("" + id);
        myref3.removeValue();

    }

   /* private void RetriveUserInfo()
    {
        UserRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username") && (dataSnapshot.hasChild("profileImage"))))
                {
                    String retriveUserName = dataSnapshot.child("username").getValue().toString();

                   // String retriveUserProfile = dataSnapshot.child("prifileImage").getValue().toString();

                    NavProfileUserName.setText(retriveUserName);

                   // String image = dataSnapshot.child("profileimage").getValue().toString();


                    //Picasso.get().load(image).placeholder(R.drawable.pro).into(NavProfileImage);





                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username")))
                {

                    String retriveUserName = dataSnapshot.child("username").getValue().toString();

                    NavProfileUserName.setText(retriveUserName);


                }
                else
                {
                    Toast.makeText(NavActivity.this, "Please set & update your profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



    private void loadextraactivis() {
        for(int i=0;i<157;i++){
            if(arr[i]==1||arr[i]==2){
                setfirebase(i);
                arr[i]=-1;
            }
        }
    }

    private void setfirebase(int i) {
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rf1, rf2, rf4;
        rf1 = firebaseDatabase.getReference("Events");
        rf2 = rf1.child(mauth.getUid());
        rf4 = rf2.child("" + eventList.get(i).getEventid());
        Eventprofile ev=eventList.get(i);
        rf4.setValue(ev).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private void deletepublicevents(int pos) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef1, myref2;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef1 = firebaseDatabase.getReference("PublicEvents");
        myref2 = myRef1.child(eventList.get(pos).getEventid());
        myref2.removeValue();

    }

}


