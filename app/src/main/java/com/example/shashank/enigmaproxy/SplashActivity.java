package com.example.shashank.enigmaproxy;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class SplashActivity extends AppCompatActivity {

    boolean internet=true,storage=false,camera=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        Nammu.init(SplashActivity.this);
        String permissions[]={


                android.Manifest.permission.CAMERA,
        };
        Nammu.askForPermission(SplashActivity.this,  Manifest.permission.INTERNET, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                internet=true;

                Nammu.askForPermission(SplashActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //startActivity(new Intent(SplashActivity.this, com.example.shashank.enigmaproxy.firebaseimplementation.Welcome.class));
                        startActivity(new Intent(SplashActivity.this, com.example.shashank.enigmaproxy.firebaseimplementation.MainActivity.class));

                        finish();
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(SplashActivity.this,"STORAGE DENIED ENABLE IT FROM SETTINGS",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });



            }

            @Override
            public void permissionRefused() {
                Toast.makeText(SplashActivity.this,"INTERNET DENIED ENABLE IT FROM SETTINGS",Toast.LENGTH_LONG).show();
                finish();
            }
        });









    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
