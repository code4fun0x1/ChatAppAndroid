package com.example.shashank.enigmaproxy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPin extends AppCompatActivity implements View.OnClickListener{

    EditText enterPin;
    FloatingActionButton checkPin;
    SharedPreferences idpref;
    SharedPreferences.Editor editor;

    private static String filename="USERID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);
        enterPin=(EditText)findViewById(R.id.enterPin);
        checkPin=(FloatingActionButton) findViewById(R.id.checkPin);
        checkPin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        idpref=getSharedPreferences(filename,0);
        if(idpref.getString("PIN","!!..!!").equals(enterPin.getText().toString())){

            if(Updater.runFlag==false) {
                Log.d("SHASHANK","SERVICE STARTED FROM onResume()");
                Intent backgroundService = new Intent(this, Updater.class);
                startService(backgroundService);
            }
            Intent i=new Intent(EnterPin.this,Welcome.class);
            editor=idpref.edit();
            editor.putBoolean("PINENTERED",true);
            editor.commit();
            startActivity(i);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong Pin",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // finish();
    }
}
