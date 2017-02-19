package com.example.shashank.enigmaproxy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText fName,lName,uName,pWord,rpWord;
    ImageButton register,gotologin;
    Intent i;
     String fname,lname,uname,pword,rpword;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fName=(EditText)findViewById(R.id.fName);
        lName=(EditText)findViewById(R.id.lName);
        uName=(EditText)findViewById(R.id.uName);
        pWord=(EditText)findViewById(R.id.pWord);
        rpWord=(EditText)findViewById(R.id.rpWord);
        register=(ImageButton) findViewById(R.id.register);
        gotologin=(ImageButton) findViewById(R.id.login);
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });

        i=new Intent(Register.this,Login.class);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname=fName.getText().toString().trim();
                lname=lName.getText().toString().trim();
                uname=uName.getText().toString().trim();
                pword=pWord.getText().toString().trim();
                rpword=rpWord.getText().toString().trim();

                if((fname.trim().equals(""))||(lname.trim().equals(""))||(uname.trim().equals(""))||(pword.trim().equals(""))||(rpword.trim().equals(""))){
                  //  Toast.makeText(getApplicationContext(),"error1",Toast.LENGTH_LONG).show();

                }else{


                    if(pword.equals(rpword)){

                        dialog=ProgressDialog.show(Register.this,"Please Wait","Registering New User",false,false);
                        StringRequest stringRequest=new StringRequest(Request.Method.POST,CentralURL.REGISTER_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if(response.trim().equals("ok")){
                                    dialog.dismiss();;
                                    //Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                                    startActivity(i);

                                }else if(response.equals("Uerror")){
                                    dialog.dismiss();
                                    uName.setText("");
                                    uName.setHint("Username already existes");

                                }else{
                                    dialog.dismiss();
                                   // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> hashmap=new HashMap<String,String>();
                                hashmap.put("fname",fname);
                                hashmap.put("lname",lname);
                                hashmap.put("uname",uname);
                                hashmap.put("password",pword);
                                hashmap.put("apassword",rpword);
                                return hashmap;
                            }

                        };
                        RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();
                        myQueue.add(stringRequest);



                    }else{
                        pWord.setText("");
                        pWord.setHint("Password did not match");
                        rpWord.setText("");
                    }

                }
            }
        });


    }





}
