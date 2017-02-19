package com.example.shashank.enigmaproxy;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shashank.enigmaproxy.databases.MyDBHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Updater extends Service {
    public final String TAG="BATMAN";
    private static String filename="USERID";

    static final int DELAY=7000;
    public static boolean runFlag=false;
    public static boolean lastUpdate=false;
    private Update update;
    public static String _id="";
    static boolean load=false;


    ImageRequest imgRequest;
    SharedPreferences idpref;
    SharedPreferences.Editor editor;
    StatusData statusData;
    String pass="";
    public int _cid=0;

    MyDBHandler dbHandler=null;
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    final static String GROUP_KEY_EMAILS = "group_key_emails";
    Random random;
    NotificationManagerCompat notificationManager;

    public Updater() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"SERVICE STRATED");
        super.onCreate();
        this.update=new Update();
        //statusData=new StatusData(this);
        dbHandler=MyDBHandler.getInstance(Updater.this);
        random=new Random();
         notificationManager =
                NotificationManagerCompat.from(Updater.this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.runFlag=false;
        this.lastUpdate=false;
        statusData.close();
        this.update.stop();
        this.update=null;
        Log.d(TAG,"SERVICE Stopped");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        idpref=getSharedPreferences(filename,0);
        _id=idpref.getString("ID","-1");
        pass=idpref.getString("password","xyz");
        editor=idpref.edit();
        editor.putInt("service",1);
        editor.commit();
        _cid=dbHandler.getLatestTID();
        if(this.runFlag==false){
            this.runFlag=true;
            this.update.start();
        }

        return START_STICKY;
    }

    private class Update extends Thread{


        public Update(){
            super("Service Updater");
        }

        @Override
        public void run() {
            Log.d(TAG,"SERVICE THREAD");
            final Updater updater=Updater.this;


            StringRequest stringRequest=new StringRequest(Request.Method.POST,CentralURL.Updater_URL1, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    JSONObject ob = null;
                    try {
                        ob = new JSONObject(response);

                        for (int i = 1; ; i++) {
                            JSONObject row = ob.getJSONObject(i+"");
                            if(row.getString("id").equals("-1")){
                                lastUpdate=false;
                                break;
                            }else {
                                lastUpdate=true;
                                String id = row.getString("id");
                                String name = row.getString("name");
                                String uid = row.getString("uid");
                                String utype = row.getString("utype");
                                String mtype = row.getString("mtype");
                                final String message = row.getString("message");
                                final String propic = row.getString("propic");
                                File f=new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),propic);
                                if(!f.exists()){
                                    Log.d(TAG, "Loadingimage"+propic);
                                    ImageDownload(propic);
                                    Log.d(TAG, "Loading done");
                                }
                                if (Integer.parseInt(row.getString("id")) > (idpref.getInt("cid", 0))) {
                                    if(mtype.equals("pic")){
                                     //   ImageDownload(message);
                                    }
                                    ContentValues values = new ContentValues();
                                    values.put(statusData.TID, id);
                                    values.put(statusData.UID, uid);
                                    values.put(statusData.UTYPE, utype);
                                    values.put(statusData.MTYPE, mtype);
                                    values.put(statusData.NAME, name);
                                    values.put(statusData.PROPIC, propic);
                                    values.put(statusData.MESSAGE, message);
                                    dbHandler.insert("timeline",values);
                                    //statusData.insert(values);
                                    //fillView(uid,utype,mtype,message,name,propic);
                                    if(!_id.equals(uid)){


                                        NotificationCompat.Builder notificaton;
                                        notificaton=new NotificationCompat.Builder(Updater.this);
                                        notificaton.setAutoCancel(true);

                                        notificaton.setSmallIcon(R.drawable.mainicon);
                                        notificaton.setTicker("@EnigmaProxy");
                                        notificaton.setWhen(System.currentTimeMillis());
                                        notificaton.setContentTitle(name.toUpperCase());
                                        notificaton.setContentText(message);


                                        String replyLabel = getResources().getString(R.string.reply_label);

                                        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                                .setLabel(replyLabel)
                                                .build();

                                        Intent ii=new Intent(Updater.this,DirectReplyHandler.class);

                                        int nid= random.nextInt();
                                        ii.putExtra("NID",nid);
                                        Intent directReplyIntent[]={ii};




                                        PendingIntent replyPendingIntent=PendingIntent.getActivities(Updater.this,0,directReplyIntent,PendingIntent.FLAG_UPDATE_CURRENT);


                                        NotificationCompat.Action action =
                                                new NotificationCompat.Action.Builder(R.drawable.ic_menu_send,
                                                        getString(R.string.label), replyPendingIntent)
                                                        .addRemoteInput(remoteInput)
                                                        .build();


                                        notificaton.setGroup(GROUP_KEY_EMAILS);
                                        notificaton.addAction(action);


                                        Intent notificationIntent=new Intent(getApplicationContext(),Welcome.class);
                                        PendingIntent p=PendingIntent.getActivities(getApplicationContext(),0, new Intent[]{notificationIntent},PendingIntent.FLAG_UPDATE_CURRENT);
                                        notificaton.setContentIntent(p);
                                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        notificaton.setSound(alarmSound);


                                        //Integer.parseInt(id)
                                        notificationManager.notify(nid,notificaton.build());
                                    }
                                    //temporary update broadcst
//                                    Intent i1=new Intent();
//                                    i1.setAction("com.example.shashank.enigmaproxy");
//                                    i1.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                                    Bundle b=new Bundle();
//                                    b.putString("uid",uid);
//                                    b.putString("name",name);
//                                    b.putString("mtype",mtype);
//                                    b.putString("utype",utype);
//                                    b.putString("propic",propic);
//                                    b.putString("message",message);
//                                    i1.putExtras(b);
//                                    sendBroadcast(i1);


                                }
                            }
                        }
                        //send broadcst regarding updates
                        _cid=dbHandler.getLatestTID();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashmap=new HashMap<String,String>();
                    hashmap.put("id",_id);
                    hashmap.put("cid",_cid+"");
                    return hashmap;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    // do not add anything here
                    return headers;
                }
            };


            while(updater.runFlag){

                Log.d(TAG,"SERVICE THREAD");
                try {
                    //do the stuff here

                    load=false;

                    RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();


                    myQueue.add(stringRequest);



                    Thread.sleep(updater.DELAY);
                } catch (InterruptedException e) {
                    //if error then stop the thread
                    updater.runFlag=false;
                    idpref=getSharedPreferences(filename,0);
                    editor=idpref.edit();
                    editor.putInt("service",0);
                    editor.commit();
                    e.printStackTrace();
                }
            }

        }

        public void ImageDownload(final String message){

            imgRequest = new ImageRequest(CentralURL.Updater_URL2+message, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    //do stuff
                    //File root = Environment.getExternalStorageDirectory();
                    File root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                   // File myDir = new File(root +"/ENIGMA/");
                   // myDir.mkdirs();
                    File file = new File (root, message);
                        try {
                            if (!file.exists ()){
                                file.createNewFile();
                                //FileOutputStream out = new FileOutputStream(file);
                                FileOutputStream out = new FileOutputStream(file,false);
                                response.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Cannot saveimage " +message);
                        }


                    load=true;


                }

            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //do stuff
                            load=true;
                            Log.d(TAG, "Error loading image " +message);
                        }
                    });
            RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();
            myQueue.add(imgRequest);
        }

    }

}
