package com.example.shashank.enigmaproxy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {



    EditText etUsername,etPassword;
    ImageButton blogin;
    ImageButton register1;
    private static String filename="USERID";
    SharedPreferences idpref;
    SharedPreferences.Editor editor;
    ImageRequest imgRequest;
    ProgressDialog loadingD,loadingAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etUsername=(EditText)findViewById(R.id.etUsername);
        register1=(ImageButton) findViewById(R.id.clickToRegister);
        blogin=(ImageButton) findViewById(R.id.bLogin);



        idpref=getSharedPreferences(filename,0);
        String username=idpref.getString("username","-1");
        String password=idpref.getString("password","-1");

        editor=idpref.edit();
        editor.putInt("service",0);
        editor.commit();

         if(idpref.getBoolean("lastAuth",false)){
           // Toast.makeText(Login.this,"Logging Cache Account",Toast.LENGTH_LONG).show();
             if(idpref.getBoolean("pinisset",false)) {
                 Intent i = new Intent(Login.this, EnterPin.class);
                 startActivity(i);
             }else{
                 Intent i = new Intent(Login.this, SetupPin.class);
                 startActivity(i);
             }
        }

        blogin.setOnClickListener(this);
        register1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == blogin) {
            v.animate();
            Login(etUsername.getText().toString(), etPassword.getText().toString());
        }
        if(v==register1){
            Intent i=new Intent(Login.this,Register.class);
            startActivity(i);
        }
    }

    public boolean Login(final String username, final String password){

        if(!username.equals("-1")){
            loadingD=ProgressDialog.show(Login.this,"Please Wait","Authentication in Progress",false);
            StringRequest stringRequest=new StringRequest(Request.Method.POST,CentralURL.LoginURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    String r[]=response.split("~");

                    if(r[0].equals("ok")){
                        loadingD.dismiss();
                       //  Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                        editor=idpref.edit();
                        editor.putString("ID",r[1]);
                        editor.putString("name",r[2]);
                        editor.putString("coverpic","xyz");
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.putInt("cid",0);
                        editor.putBoolean("lastAuth",true);
                        editor.commit();
                        downloadProfilePicForInitialSetup(r[3]);

                    }else{
                        Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                        loadingD.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                        loadingD.dismiss();
                    Toast.makeText(Login.this,error.toString(),Toast.LENGTH_LONG).show();
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashmap=new HashMap<String,String>();
                    hashmap.put("username",username);
                    hashmap.put("password",password);
                    return hashmap;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    // do not add anything here
                    return headers;
                }

            };
            RequestQueue myQueue= MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
            myQueue.add(stringRequest);



        }
        if(username.equals("-1")){
          //  Toast.makeText(Login.this,"Invalid Details !!",Toast.LENGTH_LONG).show();
            editor=idpref.edit();
            editor.putBoolean("lastAuth",false);
            editor.commit();
        }
        return false;
    }


    public void downloadProfilePicForInitialSetup(final String message) {
        loadingAssets = ProgressDialog.show(Login.this, "Please Wait", "Loading Assets", false);

            imgRequest = new ImageRequest(CentralURL.Updater_URL2 + message, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    String TAG="canthinkcando";
                    Log.d(TAG, CentralURL.Updater_URL2+message);
                    File root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(root , message);
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(getApplicationContext(),"File Deleted",Toast.LENGTH_SHORT).show();
                    }

                        if(response!=null) {
                            try {
                                if(!file.exists()){
                                    file.createNewFile();
                                }
                                //FileOutputStream out = new FileOutputStream(file);
                                FileOutputStream fout=new FileOutputStream(file,false);
                                response.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                                fout.flush();
                                fout.close();
                                editor = idpref.edit();
                                editor.putString("propic", message);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "File Saved", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                                editor = idpref.edit();
                                editor.putString("propic", "xyz");
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "Error Loading The Profile Pic", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Responce Null", Toast.LENGTH_SHORT).show();

                        }

                    loadingAssets.dismiss();
                    Intent i=new Intent(Login.this,SetupPin.class);
                    startActivity(i);
                }

            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //do stuff
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            loadingAssets.dismiss();
                            Intent i=new Intent(Login.this,SetupPin.class);
                            startActivity(i);
                        }
                    });

        RequestQueue myQueue= MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        myQueue.add(imgRequest);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

