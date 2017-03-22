package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shashank.enigmaproxy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final int PIC_INTENT_CODE = 12;
    private EditText fName,lName,uName,pWord,rpWord;
    private ImageButton register,gotologin;
    Intent i;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private DatabaseReference mChat;
    private StorageReference mStorage;
    private String fname,lname,uname,pword,rpword;
    private ProgressDialog dialog;
    private Uri propicuri=null;
    private CircularImageView picView;
    private Bitmap bitmap=null;
    private FloatingActionButton addPropic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("users");
        mRef.keepSynced(true);
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(firebaseAuth.getCurrentUser()!=null){
//                    i=new Intent(RegisterActivity.this,Welcome.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                }
            }
        };
        fName=(EditText)findViewById(R.id.fName);
        lName=(EditText)findViewById(R.id.lName);
        uName=(EditText)findViewById(R.id.uName);
        pWord=(EditText)findViewById(R.id.pWord);
        rpWord=(EditText)findViewById(R.id.rpWord);
        picView=(CircularImageView)findViewById(R.id.propic);
        addPropic=(FloatingActionButton)findViewById(R.id.add_propic);


        picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                Intent chooser = Intent.createChooser(i, "SELECT IMAGE");
                if (chooser.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(i, PIC_INTENT_CODE);
            }
        });

        addPropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                Intent chooser = Intent.createChooser(i, "SELECT IMAGE");
                if (chooser.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(i, PIC_INTENT_CODE);
            }
        });



        register=(ImageButton) findViewById(R.id.register);
        gotologin=(ImageButton) findViewById(R.id.login);
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(RegisterActivity.this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=ProgressDialog.show(RegisterActivity.this,"Registering User","In Progress",false,false);

                fname=fName.getText().toString().trim();
                lname=lName.getText().toString().trim();
                uname=uName.getText().toString().trim();
                pword=pWord.getText().toString().trim();
                rpword=rpWord.getText().toString().trim();

                if((fname.trim().equals(""))||(lname.trim().equals(""))||(uname.trim().equals(""))||(pword.trim().equals(""))||(rpword.trim().equals(""))){
                    //  Toast.makeText(getApplicationContext(),"error1",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }else{
                    if(pword.equals(rpword)){
                        mAuth.createUserWithEmailAndPassword(uname,pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    uploadProfilepic();
                                  //  Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                }else{
                                   dialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        dialog.dismiss();
                    }

                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PIC_INTENT_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                propicuri = result.getUri();
                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),propicuri);
                    picView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void uploadProfilepic() {
        final String uid=mAuth.getCurrentUser().getUid();
        mRef=FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference tRef=mRef.child(uid);

        UserModel newUser=new UserModel();
        newUser.setName(fname+" "+lname);
        newUser.setUid(uid);
        if(uName.getText().toString()!=null)
        newUser.setEmail(uName.getText().toString());
        newUser.setPropic("default");
       // tRef.child("name").setValue(fname+" "+lname);
        //newUserObject.child("name").setValue(fname+" "+lname);
        //tRef.child("propic").setValue("default");
        tRef.setValue(newUser);
        if(propicuri==null){
           // tRef.child("propic").setValue("default");
            if(mAuth.getCurrentUser()!=null){
                dialog.dismiss();
                i=new Intent(RegisterActivity.this,Welcome.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }else{
            mStorage=FirebaseStorage.getInstance().getReference().child("profilepic");
            StorageReference ref=mStorage.child(uid);

            ref.putFile(propicuri).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(RegisterActivity.this, "URL:"+taskSnapshot.getDownloadUrl(), Toast.LENGTH_SHORT).show();
                    String propicURL=taskSnapshot.getDownloadUrl().toString();
                   tRef.child("propic").setValue(propicURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           dialog.dismiss();
                           if(task.isSuccessful()){

                               if(mAuth.getCurrentUser()!=null){
                                   i=new Intent(RegisterActivity.this,MainActivity.class);
                                   i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   startActivity(i);
                                   finish();
                               }
                           }else{
                               Toast.makeText(RegisterActivity.this, "propic url eror", Toast.LENGTH_SHORT).show();

                           }

                       }
                   });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error Uploading ProfilePic", Toast.LENGTH_SHORT).show();

                }
            });
        }




    }


    @Override
    protected void onStart() {
        super.onStart();
     //   mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
     //   mAuth.removeAuthStateListener(mAuthListener);
    }
}
