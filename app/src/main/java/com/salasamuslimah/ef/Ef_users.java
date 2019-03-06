package com.salasamuslimah.ef;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.salasamuslimah.ef.NavActivity.arr;
import static com.salasamuslimah.ef.NavActivity.eventList;


import java.util.ArrayList;
import java.util.List;

public class Ef_users extends AppCompatActivity {

   static List<Setupaactivityclass>users;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref1,ref2;
    FirebaseAuth mAuth;
    ListView list;
    CustomAdapter1 customAdapter1;
    CustomAdapter customAdapter;
    static  String key="";
    boolean connected=false;
    int login=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ef_users);
        list=findViewById(R.id.listview1);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        ref1 =firebaseDatabase.getReference("Users");
        login=getIntent().getIntExtra("login",-1);

        if(login==-1) {
            loadextraactivis();
        }
        if(users==null){
            users=new ArrayList<>();
            loadusers();

        }
        else if (users.size()!=0){
            users.clear();
            loadusers();
        }
        else loadusers();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(chkInternetconnection()){
                    String s = users.get(i).getUserid();
                    key=s;
                    showActionDialog(i,s);
                }
                else Toast.makeText(Ef_users.this, "Need internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void thread1() {
        Toast.makeText(Ef_users.this, "Please wait data loading process is running\nIt will take few moments", Toast.LENGTH_SHORT).show();
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

    private void loadusers() {
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(Ef_users.this, "Error occured while loading data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getInformation(String key) {
        ref2 = ref1.child(key);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Setupaactivityclass setupaactivityclass = dataSnapshot.getValue(Setupaactivityclass.class);
                users.add(setupaactivityclass);

//               Log.e("data",""+eventList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Ef_users.this, "Error occured while loading data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void thread() {
        Toast.makeText(Ef_users.this, "Please wait data loading process is running\nIt will take few moments", Toast.LENGTH_SHORT).show();
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
        if(users.size()!=0) {
            customAdapter1 = new CustomAdapter1(Ef_users.this, users);
            list.setAdapter(customAdapter1);
            Toast.makeText(Ef_users.this, "Only public events will be shown", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(Ef_users.this, "There is "+users.size()+" users", Toast.LENGTH_SHORT).show();
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


    private void showActionDialog(final int position, final String key) {
        CharSequence colors[] = new CharSequence[]{"Show Events"};

        AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this);

        alertDialogBuidler.setTitle("Choose Option")
                .setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //show event or lagi
                            if(chkInternetconnection()){
                                Intent intent=new Intent(Ef_users.this,Otherusersevents.class);
                                //intent.putExtra("key",key);
                                intent.putExtra("login",login);
                                startActivity(intent);

                            }
                            else Toast.makeText(Ef_users.this, "Need internet access", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
       // eventList=null;

        if(login==-1){

            Intent intent = new Intent(Ef_users.this, NavActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        else{
            Intent intent = new Intent(Ef_users.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        eventList=null;

    }


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





}
