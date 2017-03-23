package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashank.enigmaproxy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "WELCOME_ACTIIVITY";

//192.168.172.1

    FloatingActionButton prosetting;
    ImageButton cameraAction,btnAccounts;
    Bitmap bmp;
    CircularImageView propic;
    ImageView coverPic;
    TextView accountName, accountId;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef,profileRefernce,chatReference;
    private StorageReference profileStorage,mediaStorage;
    ProgressDialog dialog;
    NavigationView navigationView;
    boolean menuAccounts=false;
    private FriendListFragment friendListFragment=null;
    private SingleFriendChatFragment singleChat=null;
    FragmentManager manager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_welcome);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            //  Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else {
            // Toast.makeText(this, "NOT NULL", Toast.LENGTH_SHORT).show();
        }
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent i=new Intent(MainActivity.this,LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };
        mRef= FirebaseDatabase.getInstance().getReference();
        chatReference=mRef.child("chat");
        profileRefernce=mRef.child("users");
        chatReference.keepSynced(true);
        profileRefernce.keepSynced(true);
        profileStorage= FirebaseStorage.getInstance().getReference().child("profilepic");
        mediaStorage=FirebaseStorage.getInstance().getReference().child("media");

        initialize();

    }


    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        btnAccounts=(ImageButton) v.findViewById(R.id.btn_accounts);
        btnAccounts.setOnClickListener(this);

        propic = (CircularImageView) v.findViewById(R.id.ndPropic);
        accountId = (TextView) v.findViewById(R.id.accountId);
        accountName = (TextView) v.findViewById(R.id.accountName);
        DatabaseReference tUser=null;
        if(mAuth.getCurrentUser()!=null) {
            tUser = profileRefernce.child(mAuth.getCurrentUser().getUid());
            tUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profilePicPath;
                    profilePicPath = String.valueOf(dataSnapshot.child("propic").getValue());
                    if (!profilePicPath.equals("default"))
                        Picasso.with(getApplicationContext()).load(profilePicPath).fit().centerCrop().into(propic);
                    accountName.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                    accountId.setText(mAuth.getCurrentUser().getEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else{
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        coverPic = (ImageView) v.findViewById(R.id.coverPic);
        //setting=(FloatingActionButton)v.findViewById(R.id.selectCover);
        prosetting=(FloatingActionButton)v.findViewById(R.id.selectNewProfilePic);
        //bsend = (FancyButton) findViewById(R.id.bsend);

        if(mAuth.getCurrentUser()!=null){
            manager=getSupportFragmentManager();
            if(friendListFragment==null){
                friendListFragment=new FriendListFragment();
            }
            friendListFragment.setOnFriendClickListener(new FriendListFragment.FriendClick() {
                @Override
                public void onFriendClicked(String uid) {

                        singleChat=SingleFriendChatFragment.newInstance(uid);

                    //singleChat.receiveUID(uid);

                    manager.beginTransaction().replace(R.id.mainFragmentHolder,singleChat).commit();

                }
            });
            manager.beginTransaction().replace(R.id.mainFragmentHolder,friendListFragment).commit();

        }




    }

    @Override
    public void onClick(View v) {

    }






    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth.getCurrentUser()==null){

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chatpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_logout){
            mAuth.signOut();
        }
        if(id==R.id.action_requests){
            getSupportFragmentManager().beginTransaction().add(new FriendRequestFragment(),null).commit();
        }
        if(id==R.id.action_find){
            startActivity(new Intent(MainActivity.this,FindFriendActivity.class));
            //getSupportActionBar().hide();

        }



        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
