package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Toast;

import com.example.shashank.enigmaproxy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    CoordinatorLayout currentHolder;
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
    public CircularImageView friendPropic;
    ActionBarDrawerToggle toggle;


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
            updatePresenceStatus();
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
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        currentHolder=(CoordinatorLayout)v.findViewById(R.id.currentHolder);
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
        prosetting.setOnClickListener(this);
        //bsend = (FancyButton) findViewById(R.id.bsend);

        if(mAuth.getCurrentUser()!=null){
            manager=getSupportFragmentManager();
            if(friendListFragment==null){
                friendListFragment=new FriendListFragment();
            }
            friendListFragment.setOnFriendClickListener(new FriendListFragment.FriendClick() {
                @Override
                public void onFriendClicked(UserModel model) {
                    if(model!=null) {
                        singleChat = SingleFriendChatFragment.newInstance(model);

                        //singleChat.receiveUID(uid);
                        manager.beginTransaction().replace(R.id.mainFragmentHolder, singleChat).addToBackStack("singlechat").commit();
                    }else{
                        Toast.makeText(MainActivity.this, "Null in MainActivity", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            manager.beginTransaction().replace(R.id.mainFragmentHolder,friendListFragment).commit();

        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22222 && resultCode == RESULT_OK  && null != data) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(MainActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadProfilePic(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadProfilePic(Uri uri){
        if(uri!=null){
            dialog=ProgressDialog.show(MainActivity.this,"Upload","In Progress",false,false);

            StorageReference mStorage;
            final String uid=mAuth.getCurrentUser().getUid();
            mRef=FirebaseDatabase.getInstance().getReference().child("users");
            final DatabaseReference tRef=mRef.child(uid);
            mStorage=FirebaseStorage.getInstance().getReference().child("profilepic");
            StorageReference ref=mStorage.child(uid);

            ref.putFile(uri).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    //  Toast.makeText(Welcome.this, "URL:"+taskSnapshot.getDownloadUrl(), Toast.LENGTH_SHORT).show();
                    String propicURL=taskSnapshot.getDownloadUrl().toString();
                    tRef.child("propic").setValue(propicURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                Picasso.with(getApplicationContext()).load(taskSnapshot.getDownloadUrl()).fit().centerCrop().into(propic);
                                Toast.makeText(MainActivity.this,"Upload OK",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Upload Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error Uploading ProfilePic", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.selectNewProfilePic:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                Intent chooser = Intent.createChooser(i, "SELECT PROFILE PIC");
                if (chooser.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(i, 22222);
                break;
        }
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
        if(id==android.R.id.home){
            Toast.makeText(this, "Holla", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
            toggle.setDrawerIndicatorEnabled(true);

        }
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


    void updatePresenceStatus(){

        // since I can connect from multiple devices, we store each connection instance separately
// any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("connections");

// stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("lastonline");

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    DatabaseReference con = myConnectionsRef.push();
                    con.setValue(Boolean.TRUE);

                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });

    }




}
