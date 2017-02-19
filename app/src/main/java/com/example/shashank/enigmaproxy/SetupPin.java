package com.example.shashank.enigmaproxy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SetupPin extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences idpref;
    SharedPreferences.Editor editor;
    private static String filename="USERID";
    EditText pin;
    FloatingActionButton setPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_pin);
        setPin=(FloatingActionButton) findViewById(R.id.setPin);
        pin=(EditText)findViewById(R.id.pin);
        setPin.setOnClickListener(this);
        idpref=getSharedPreferences(filename,0);



    }

    @Override
    public void onClick(View v) {
        editor=idpref.edit();
        editor.putString("PIN",pin.getText().toString());
        editor.putBoolean("pinisset",true);
        editor.commit();
        Intent i=new Intent(SetupPin.this,Welcome.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
