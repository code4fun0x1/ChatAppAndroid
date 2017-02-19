package com.example.shashank.enigmaproxy;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DirectReplyHandler extends AppCompatActivity {


    private static final String KEY_TEXT_REPLY = "key_text_reply";
    Bundle b;
    String message;
    SharedPreferences idpref;
    SharedPreferences.Editor editor;
    private static String filename="USERID";
    private int nid=404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_direct_reply_handler);

            message=getMessageText(getIntent()).toString();


        nid=getIntent().getIntExtra("NID",404);
        idpref=getSharedPreferences(filename,0);
        sendDirectMessage(message);

    }




    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }


    private void sendDirectMessage(final String message){

        final Context context=getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CentralURL.MESSAGE_SENT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("~")) {
                   //message sent


                    // Build a new notification, which informs the user that the system
                    // handled their interaction with the previous notification.
                    Notification repliedNotification =
                            new Notification.Builder(context)
                                    .setSmallIcon(R.drawable.mainicon)
                                    .setContentText("Message successfully sent")
                                    .build();

                    // Issue the new notification.
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(context);
                    notificationManager.notify(nid, repliedNotification);




                }else{
                    Notification repliedNotification =
                            new Notification.Builder(context)
                                    .setSmallIcon(R.drawable.mainicon)
                                    .setContentText("Error Sending Message!!")
                                    .build();

                    // Issue the new notification.
                    NotificationManagerCompat notificationManager =
                            NotificationManagerCompat.from(context);
                    notificationManager.notify(nid, repliedNotification);

                }
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                notificationManager.cancelAll();
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Notification repliedNotification =
                        new Notification.Builder(context)
                                .setSmallIcon(R.drawable.mainicon)
                                .setContentText("Error Sending Message!")
                                .build();

                // Issue the new notification.
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                notificationManager.notify(nid, repliedNotification);
                finish();


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashmap = new HashMap<String, String>();
                String id=idpref.getString("ID","-404");
                if(id.equals("-404")){
                    finish();
                }
                hashmap.put("id", id);
                hashmap.put("message", message);


                return hashmap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // do not add anything here
                return headers;
            }
        };
        RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();


        myQueue.add(stringRequest);



    }






}
