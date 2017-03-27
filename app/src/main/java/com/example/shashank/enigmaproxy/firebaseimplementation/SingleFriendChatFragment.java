package com.example.shashank.enigmaproxy.firebaseimplementation;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.enigmaproxy.FullscreenActivity;
import com.example.shashank.enigmaproxy.ListModel;
import com.example.shashank.enigmaproxy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.google.android.gms.internal.zzs.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleFriendChatFragment extends Fragment implements View.OnClickListener {



    TextView tt;
    View mainView;
    LayoutInflater inflaater;
    ImageView bsend;
    View viewUploadPanel;
    boolean uploadPanelOnDisplay=false;
    ImageButton cameraAction,galleryAction,revealPanel;
    Bitmap bmp;
    CircularImageView friendPropic;
    View currentUserPropic;
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
    private DatabaseReference mRef,profileRefernce, myChatReference,friendChatReference;
    private StorageReference profileStorage,mediaStorage;
    RecyclerView recyclerview;
    ProgressDialog dialog;
    LinearLayoutManager lmanager=null;
    NavigationView navigationView;
    boolean menuAccounts=false;
    UserModel currentFriend=new UserModel();
    public SingleFriendChatFragment() {
        // Required empty public constructor
    }






    public static SingleFriendChatFragment newInstance(UserModel u)  {

        Bundle args = new Bundle();
        args.putString("uid",u.getUid());
        args.putString("name",u.getName());
        args.putString("email",u.getEmail());

        args.putString("propic",u.getPropic());

        SingleFriendChatFragment fragment = new SingleFriendChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView=inflater.inflate(R.layout.fragment_single_friend_chat, container, false);
        inflaater=inflater;
        try{
            currentFriend.setUid(getArguments().getString("uid"));
            currentFriend.setName(getArguments().getString("name"));
            currentFriend.setEmail(getArguments().getString("email"));
            currentFriend.setPropic(getArguments().getString("propic"));

        }catch (Exception e){
            e.printStackTrace();
        }

        initialize();


        return mainView;
    }


    private void initialize() {

        mAuth=FirebaseAuth.getInstance();

        //this is the reference
        mRef= FirebaseDatabase.getInstance().getReference().child("users");
        profileRefernce=mRef.child(mAuth.getCurrentUser().getUid()).child("friends").child(currentFriend.getUid());
        myChatReference =profileRefernce.child("chat");
        friendChatReference=mRef.child(currentFriend.getUid()).child("friends").child(mAuth.getCurrentUser().getUid()).child("chat");

        myChatReference.keepSynced(true);
        profileRefernce.keepSynced(true);
        profileStorage= FirebaseStorage.getInstance().getReference().child("profilepic");
        mediaStorage=FirebaseStorage.getInstance().getReference().child("media");
        recyclerview=(RecyclerView)mainView.findViewById(R.id.chatBody);

        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        revealPanel=(ImageButton)mainView.findViewById(R.id.revealPanel);
        revealPanel.setOnClickListener(this);
        cameraAction=(ImageButton)mainView.findViewById(R.id.camera_action);
        cameraAction.setOnClickListener(this);
        galleryAction=(ImageButton)mainView.findViewById(R.id.gallery_action);
        galleryAction.setOnClickListener(this);
        viewUploadPanel=mainView.findViewById(R.id.uploadRevealPanel);
//        // setting.setOnClickListener(this);
//        prosetting.setOnClickListener(this);
//
//
        etmessage = (EditText) mainView.findViewById(R.id.etMessage);
        bsend = (ImageView)mainView. findViewById(R.id.bsend);
        bsend.setOnClickListener(this);


        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setAdapter(new FireAdapter(null,0,null,null));
        RecyclerView.ItemAnimator itemanimator = new DefaultItemAnimator();
        itemanimator.setAddDuration(400);
        itemanimator.setRemoveDuration(400);
        recyclerview.setItemAnimator(itemanimator);
        recyclerview.setSelected(true);

    }




    public void receiveUID(String uid){
        tt.setText(uid);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.revealPanel) {

           // handleCameraAction();
            if(uploadPanelOnDisplay){
                uploadPanelOnDisplay=false;
                //its on display so we have to close it
                // get the center for the clipping circle
                int cx = viewUploadPanel.getRight();
                int cy = viewUploadPanel.getBottom();

// get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(viewUploadPanel.getWidth(), viewUploadPanel.getHeight());

// create the animation (the final radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(viewUploadPanel, 0, cy, initialRadius, 0);

// make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewUploadPanel.setVisibility(View.GONE);
                    }
                });

// start the animation
                anim.start();
            }else{
                uploadPanelOnDisplay=true;
                //its closed so open it
                viewUploadPanel.setVisibility(View.INVISIBLE);
                int cx = viewUploadPanel.getRight();
                int cy = viewUploadPanel.getBottom();

// get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(viewUploadPanel.getWidth(), viewUploadPanel.getHeight());

// create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(viewUploadPanel, 0, cy, 0, finalRadius);

// make the view visible and start the animation
                viewUploadPanel.setVisibility(View.VISIBLE);
                anim.start();
            }

        }
        if(R.id.camera_action==v.getId()){
            handleCameraAction();
        }
        if(v.getId()==R.id.gallery_action){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            Intent chooser = Intent.createChooser(i, "SELECT IMAGE");
            if (chooser.resolveActivity(getActivity().getPackageManager()) != null)
                startActivityForResult(i, 999);
        }


        if (v.getId() == R.id.bsend) {

            String message=etmessage.getText().toString().trim();
            if(!message.trim().equals("")){
                ChatModel c=new ChatModel();
                c.setMessage(message);
                c.setMtype("text");
                c.setUid(mAuth.getCurrentUser().getUid());
                myChatReference.push().setValue(c);
                friendChatReference.push().setValue(c);
                etmessage.setText("");

            }
            }

    }

    private void handleCameraAction() {

        // Handle the camera action
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File stoDir=getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            File image=File.createTempFile(timestamp+"imageEnigma",".jpg",stoDir);

            fullSizeImagePath=image.getAbsolutePath();
            if(image!=null){
                Uri photoURI= FileProvider.getUriForFile(getActivity(),"com.example.shashank.enigmaproxy.fileprovider",image);
                i.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(i, 555);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode==555 && resultCode == RESULT_OK ) {
            uploadPicToFirebase(Uri.fromFile(new File(fullSizeImagePath)));
           // uploadPicToFirebase(data.getData());

        }else if (requestCode!=RESULT_OK){
            Toast.makeText(getActivity(), "NOT OK", Toast.LENGTH_SHORT).show();
        }


        if(requestCode == 999 && resultCode == RESULT_OK  && null != data){
            uploadPicToFirebase(data.getData());
        }

    }


    private void uploadPicToFirebase(Uri uri){

        dialog=ProgressDialog.show(getActivity(),"Uploading","In Progress",false);
        dialog.setMax(100);
        mediaStorage.child(mAuth.getCurrentUser().getUid()+""+ System.currentTimeMillis()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ChatModel c=new ChatModel();
                c.setMessage(taskSnapshot.getDownloadUrl().toString());
                c.setMtype("pic");
                c.setUid(mAuth.getCurrentUser().getUid());
                myChatReference.push().setValue(c);
                friendChatReference.push().setValue(c);

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







    public class FireAdapter extends FirebaseRecyclerAdapter<ChatModel,RecyclerView.ViewHolder> {


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
            super(ChatModel.class, R.layout.chat_left, RecyclerView.ViewHolder.class, myChatReference);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;
            if(viewType==0){
                v=inflaater.inflate(R.layout.left_single_chat_card,parent,false);
                return new FireHolder(v);
            }
            else if(viewType==2){
                v=inflaater.inflate(R.layout.right_single_chat_card,parent,false);
                return new FireHolder(v);

            }
            else if(viewType==1){
                v=inflaater.inflate(R.layout.left_image_single_chat_card,parent,false);
                return new FireHolderImage(v);

            }
            else if(viewType==3){
                v=inflaater.inflate(R.layout.right_image_single_chat_card,parent,false);
                return new FireHolderImage(v);
            }
            return null;
        }

        @Override
        protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, final ChatModel model, int position) {

            DatabaseReference tUser=mRef.child(model.getUid());



            if(model.getMtype().equals("pic")){

                final FireHolderImage holderImage=(FireHolderImage)viewHolder;

                Picasso.with(getContext()).load(model.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().into(holderImage.imagecontent, new Callback() {
                    @Override
                    public void onSuccess() {
                       // holderImage.progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                     //   holderImage.progressbar.setVisibility(View.GONE);
                        Picasso.with(getContext()).load(model.getMessage()).fit().centerCrop().into(holderImage.imagecontent);
                    }
                });

                holderImage.imagecontent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getContext(),FullscreenActivity.class);
                        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(getActivity(),holderImage.imagecontent,"zoom");
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
//                tUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String profilePicPath;
//                        profilePicPath=String.valueOf(dataSnapshot.child("propic").getValue());
//                        if(!profilePicPath.equals("default"))
//                            Picasso.with(getContext()).load(profilePicPath).fit().centerCrop().into(holderText.circularimage);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
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
           // circularimage=(CircularImageView) v.findViewById(R.id.networkImageView);
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
      //  ProgressBar progressbar;
      //  FrameLayout secondryImageLayout;

        public FireHolderImage(View itemView) {
            super(itemView);
            v=itemView;
          //  circularimage=(CircularImageView) v.findViewById(R.id.networkImageView);
            imagecontent=(ImageView)v.findViewById(R.id.ivContent);
            // title=(TextView)v.findViewById(R.id.tvName);
          //  secondryImageLayout=(FrameLayout)v.findViewById(R.id.secondry_imagelayout);
          //  progressbar=(ProgressBar)v.findViewById(R.id.progressBar);
        }



    }

}
