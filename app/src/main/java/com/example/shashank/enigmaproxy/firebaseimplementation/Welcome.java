package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.enigmaproxy.FullscreenActivity;
import com.example.shashank.enigmaproxy.ListModel;
import com.example.shashank.enigmaproxy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Welcome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "WELCOME_ACTIIVITY";

//192.168.172.1

    FloatingActionButton setting,prosetting,bsend;
    ImageButton cameraAction,btnAccounts;
    Bitmap bmp;
    CircularImageView propic;
    ImageView coverPic;
    TextView accountName, accountId;
    private Toolbar toolbar;
    String fullSizeImagePath=null;
    //FloatingActionButton bsend;
    //FancyButton bsend;
    EditText etmessage;
    static String _id = "", _name = "", _propic = "";
    public static int _cid = 0;
    //CustomAdapter adapter;
    static ListModel model;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef,profileRefernce,chatReference;
    private StorageReference profileStorage,mediaStorage;
    RecyclerView recyclerview;
    ProgressDialog dialog;
    LinearLayoutManager lmanager=null;
    NavigationView navigationView;
    boolean menuAccounts=false;
    



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_welcome);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()==null){
          //  Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(Welcome.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else {
           // Toast.makeText(this, "NOT NULL", Toast.LENGTH_SHORT).show();
        }

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent i=new Intent(Welcome.this,LoginActivity.class);
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
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(new FireAdapter(null,0,null,chatReference));

        RecyclerView.ItemAnimator itemanimator = new DefaultItemAnimator();
        itemanimator.setAddDuration(400);
        itemanimator.setRemoveDuration(400);
        recyclerview.setItemAnimator(itemanimator);
        recyclerview.setSelected(true);


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
            Intent i=new Intent(Welcome.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        coverPic = (ImageView) v.findViewById(R.id.coverPic);
        //setting=(FloatingActionButton)v.findViewById(R.id.selectCover);
        prosetting=(FloatingActionButton)v.findViewById(R.id.selectNewProfilePic);
      //  cameraAction=(ImageButton)findViewById(R.id.camera_action);
      //  cameraAction.setOnClickListener(this);
       // setting.setOnClickListener(this);
      //  prosetting.setOnClickListener(this);


      //  etmessage = (EditText) findViewById(R.id.etMessage);
      //  bsend = (FloatingActionButton) findViewById(R.id.bsend);
        //bsend = (FancyButton) findViewById(R.id.bsend);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerview = (RecyclerView) findViewById(R.id.mainbody);
        //bsend.setOnClickListener(this);


        //hide the soft keyboard on startup
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etmessage.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);



//        String propicPath = idpref.getString("propic", "xyz");
//        String coverPicPath = idpref.getString("coverpic", "xyz");
//
//        File file;
//        file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), propicPath);
//        if (file.exists()) {
//            bmp = BitmapFactory.decodeFile(String.valueOf(file));
//            propic.setImageBitmap(bmp);
//        } else {
//            propic.setImageResource(R.drawable.default_profile_pic);
//        }
//        file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), coverPicPath);
//        if (file.exists()) {
//            bmp = BitmapFactory.decodeFile(String.valueOf(file));
//            coverPic.setImageBitmap(bmp);
//        } else {
//            coverPic.setImageResource(R.drawable.photodefault);
//        }


    }



    public class FireAdapter extends FirebaseRecyclerAdapter<ChatModel,RecyclerView.ViewHolder>{


        /**
         * @param modelClass      Firebase will marshall the data at a location into
         *                        an instance of a class that you provide
         * @param modelLayout     This is the layout used to represent a single item in the list.
         *                        You will be responsible for populating an instance of the corresponding
         *                        view with the data from an instance of modelClass.
         * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
         * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
         *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
         */
        public FireAdapter(Class<ChatModel> modelClass, int modelLayout, Class<FireHolder> viewHolderClass, Query ref) {
            super(ChatModel.class, R.layout.chat_left, RecyclerView.ViewHolder.class, chatReference);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;
            if(viewType==0){
                v=getLayoutInflater().inflate(R.layout.chat_left,parent,false);
                return new FireHolder(v);
            }
            else if(viewType==2){
                v=getLayoutInflater().inflate(R.layout.chat_right,parent,false);
                return new FireHolder(v);

            }
            else if(viewType==1){
                v=getLayoutInflater().inflate(R.layout.chat_left_image,parent,false);
                return new FireHolderImage(v);

            }
            else if(viewType==3){
                v=getLayoutInflater().inflate(R.layout.chat_right_image,parent,false);
                return new FireHolderImage(v);
            }
            return null;
        }

        @Override
        protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, final ChatModel model, int position) {

            DatabaseReference tUser=profileRefernce.child(model.getUid());



            if(model.getMtype().equals("pic")){

                final FireHolderImage holderImage=(FireHolderImage)viewHolder;

                Picasso.with(getApplicationContext()).load(model.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().into(holderImage.imagecontent, new Callback() {
                    @Override
                    public void onSuccess() {
                        holderImage.progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holderImage.progressbar.setVisibility(View.GONE);
                        Picasso.with(getApplicationContext()).load(model.getMessage()).fit().centerCrop().into(holderImage.imagecontent);
                    }
                });

                holderImage.imagecontent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(Welcome.this,FullscreenActivity.class);
                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(Welcome.this,holderImage.imagecontent,"zoom");
                        i.putExtra("path",model.getMessage());
                        startActivity(i,options.toBundle());
                    }
                });

//                tUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String profilePicPath;
//                        profilePicPath=String.valueOf(dataSnapshot.child("propic").getValue());
//                        if(!profilePicPath.equals("default"))
//                            Picasso.with(Welcome.this).load(profilePicPath).resize(60,60).into(holderImage.circularimage);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }else {
                final FireHolder  holderText=(FireHolder)viewHolder;
                holderText.content.setText(model.getMessage());
                tUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String profilePicPath;
                        profilePicPath=String.valueOf(dataSnapshot.child("propic").getValue());
                        if(!profilePicPath.equals("default"))
                            Picasso.with(Welcome.this).load(profilePicPath).fit().centerCrop().into(holderText.circularimage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        }



        @Override
        public int getItemViewType(int position) {
            ChatModel model=getItem(position);
            if(model.getUid().equals(mAuth.getCurrentUser().getUid())){
                if (!model.getMtype().equals("pic"))
                    return 0;
                else
                    return 1;
            }else{
                if (!model.getMtype().equals("pic"))
                    return 2;
                else
                    return 3;
            }
        }
    }



    public class FireHolder extends RecyclerView.ViewHolder{

        View v;
        CircularImageView circularimage;
        // TextView title;
        TextView content;

        public FireHolder(View itemView) {
            super(itemView);
            v=itemView;
            circularimage=(CircularImageView) v.findViewById(R.id.networkImageView);
            // title=(TextView)v.findViewById(R.id.tvName);

            content=(TextView)v.findViewById(R.id.tvContent);
        }



    }

    public class FireHolderImage extends RecyclerView.ViewHolder{

        View v;
        CircularImageView circularimage;
        // TextView title;
        ImageView imagecontent;
        //ImageButton loadImageButton,overflowButton;
        ProgressBar progressbar;
        FrameLayout secondryImageLayout;

        public FireHolderImage(View itemView) {
            super(itemView);
            v=itemView;
            circularimage=(CircularImageView) v.findViewById(R.id.networkImageView);
            imagecontent=(ImageView)v.findViewById(R.id.ivContent);
            // title=(TextView)v.findViewById(R.id.tvName);
            secondryImageLayout=(FrameLayout)v.findViewById(R.id.secondry_imagelayout);
            progressbar=(ProgressBar)v.findViewById(R.id.progressBar);
        }



    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_accounts) {
            navigationView.getMenu().clear();
            if(menuAccounts){
                navigationView.inflateMenu(R.menu.activity_new_welcome_drawer);
                menuAccounts=false;
            }else{
                navigationView.inflateMenu(R.menu.new_welcome);
                menuAccounts=true;
            }

        }
//        if (v.getId() == R.id.camera_action) {
//
//            handleCameraAction();
//
//        }
//        if(v.getId()==R.id.selectCover){
//            Intent cover = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(cover, 12345);
//        }
        if(v.getId()==R.id.selectNewProfilePic){

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            Intent chooser = Intent.createChooser(i, "SELECT PROFILE PIC");
            if (chooser.resolveActivity(getPackageManager()) != null)
                startActivityForResult(i, 22222);

        }

//        if (v.getId() == R.id.bsend) {
//
//            String message=etmessage.getText().toString();
//            ChatModel c=new ChatModel();
//            c.setMessage(message);
//            c.setMtype("text");
//            c.setUid(mAuth.getCurrentUser().getUid());
//            chatReference.push().setValue(c);
//            etmessage.setText("");
//        }


    }

    private void handleCameraAction() {

        // Handle the camera action
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File stoDir=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            File image=File.createTempFile(timestamp+"imageEnigma",".jpg",stoDir);

            fullSizeImagePath=image.getAbsolutePath();
            if(image!=null){
                Uri photoURI= FileProvider.getUriForFile(this,"com.example.shashank.enigmaproxy.fileprovider",image);
                i.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(i, 555);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode==555) {
            uploadPicToFirebase(Uri.fromFile(new File(fullSizeImagePath)));
        }
        if (requestCode==12345 && resultCode == RESULT_OK && null != data) {

        }
        if (requestCode == 22222 && resultCode == RESULT_OK  && null != data) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Welcome.this);
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
        if(requestCode == 999 && resultCode == RESULT_OK  && null != data){
           uploadPicToFirebase(data.getData());
        }

    }

    private void uploadProfilePic(Uri uri){
        if(uri!=null){
            dialog=ProgressDialog.show(Welcome.this,"Upload","In Progress",false,false);

            StorageReference mStorage;
            final String uid=mAuth.getCurrentUser().getUid();
            mRef=FirebaseDatabase.getInstance().getReference().child("users");
            final DatabaseReference tRef=mRef.child(uid);
            mStorage=FirebaseStorage.getInstance().getReference().child("profilepic");
            StorageReference ref=mStorage.child(uid);

            ref.putFile(uri).addOnSuccessListener(Welcome.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                Toast.makeText(Welcome.this,"Upload OK",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Welcome.this, "Upload Error", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Welcome.this, "Error Uploading ProfilePic", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    private void uploadPicToFirebase(Uri uri){

        dialog=ProgressDialog.show(Welcome.this,"Uploading","In Progress",false);
        dialog.setMax(100);
        mediaStorage.child(mAuth.getCurrentUser().getUid()+""+ System.currentTimeMillis()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ChatModel c=new ChatModel();
                c.setMessage(taskSnapshot.getDownloadUrl().toString());
                c.setMtype("pic");
                c.setUid(mAuth.getCurrentUser().getUid());
                chatReference.push().setValue(c);
                dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress= (int) (taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount());
                dialog.setProgress(progress);
                Log.d(TAG, "onProgress: UPLAODING"+progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });


    }




    private String getMimeType(String path) {

        String ex= MimeTypeMap.getFileExtensionFromUrl(path);
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(ex);
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
            startActivity(new Intent(Welcome.this,FindFriendActivity.class));
            //getSupportActionBar().hide();

        }



        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        if (id == R.id.capture) {
//            handleCameraAction();
//
//        } else if (id == R.id.attachment) {
//            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            i.setType("image/*");
//            Intent chooser = Intent.createChooser(i, "SELECT IMAGE");
//            if (chooser.resolveActivity(getPackageManager()) != null)
//                startActivityForResult(i, 999);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
