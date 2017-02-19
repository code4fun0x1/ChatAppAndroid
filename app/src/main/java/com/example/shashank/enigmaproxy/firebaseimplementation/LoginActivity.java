package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shashank.enigmaproxy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername,etPassword;
    private ImageButton blogin;
    private ImageButton register1;
    private ProgressDialog loadingD,loadingAssets;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword=(EditText)findViewById(R.id.etPassword);
        etUsername=(EditText)findViewById(R.id.etUsername);
        register1=(ImageButton) findViewById(R.id.clickToRegister);
        blogin=(ImageButton) findViewById(R.id.bLogin);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent i=new Intent(LoginActivity.this,Welcome.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
              //  DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
              //  mRef.child("iuhfgf").setValue("sefsefesfesfesf");
            }
        });
        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

    }


    public void signIn(){
        String email=etUsername.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        Toast.makeText(this,email+""+password,Toast.LENGTH_SHORT).show();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            loadingD=ProgressDialog.show(this,"Logging","Attempting Login",false,false);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingD.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "user_id\n"+mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
            );
        }else{
            Toast.makeText(this, "InputFields Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
