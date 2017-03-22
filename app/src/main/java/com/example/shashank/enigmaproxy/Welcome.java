package com.example.shashank.enigmaproxy;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shashank.enigmaproxy.databases.MyDBHandler;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Welcome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "WELCOME_ACTIIVITY";

//192.168.172.1

    FloatingActionButton setting,prosetting;
    ImageButton cameraAction;
    Bitmap bmp;
    CircularImageView propic;
    ImageView coverPic;
    TextView accountName, accountId;
    private Toolbar toolbar;
    private BroadcastReceiver receiver;
    FloatingActionButton bsend;
    EditText etmessage;
    static String _id = "", _name = "", _propic = "";
    public static int _cid = 0;
    //StatusData statusData;
    MyDBHandler dbhandler=null;
    SharedPreferences idpref;
    SharedPreferences.Editor editor;
    private static String filename = "USERID";
    List<ListModel> row = new ArrayList<>();
    //CustomAdapter adapter;
    static ListModel model;
    IntentFilter filter;
    static boolean ispaused = true;
    RecyclerView recyclerview;
    NewImprovedAdapter adapter;
    Bitmap bitmap;
    String fullSizeImagePath="";
    ProgressDialog dialog;
    FragmentManager manager;
    LinearLayoutManager lmanager=null;

    /* Thread profiilePicUpdaterThread=null;
    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idpref = getSharedPreferences(filename, 0);
        setContentView(R.layout.activity_new_welcome);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        propic = (CircularImageView) v.findViewById(R.id.ndPropic);
        coverPic = (ImageView) v.findViewById(R.id.coverPic);
       // setting=(FloatingActionButton)v.findViewById(R.id.selectCover);
        prosetting=(FloatingActionButton)v.findViewById(R.id.selectNewProfilePic);
       // cameraAction=(ImageButton)findViewById(R.id.camera_action);
        cameraAction.setOnClickListener(this);
        setting.setOnClickListener(this);
        prosetting.setOnClickListener(this);

        accountId = (TextView) v.findViewById(R.id.accountId);
        accountName = (TextView) v.findViewById(R.id.accountName);
        initialize();

        ispaused = false;
        //get the preference

        //statusData = new StatusData(this);
        dbhandler=MyDBHandler.getInstance(Welcome.this);
        dbhandler.setOnDBUpdateListener(new MyDBHandler.OnDBUpdateListener() {
            @Override
            public void onUpdate(Bundle b) {
                getBroadcastChat(b);
            }
        });

              filter = new IntentFilter("com.example.shashank.enigmaproxy");
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//              //  getBroadcastChat(intent);
//            }
//        };

        //initial setup
        editor = idpref.edit();
        editor.putInt("cid", 0);
        editor.commit();

        getChat();


        adapter = new NewImprovedAdapter(row,getApplicationContext());
         lmanager=new LinearLayoutManager(this);
       // lmanager.setStackFromEnd(true);
        recyclerview.setLayoutManager(lmanager);
       // recyclerview.setAdapter(adapter);
        recyclerview.setAdapter(adapter);


        manager=getSupportFragmentManager();
        //custom listener for changing image in fragment
        adapter.setOnImageClickListener(new NewImprovedAdapter.OnImageClickListener() {
            @Override
            public void changeFragment(String path,ImageView iv) {
              //  FragmentTransaction t=manager.beginTransaction();

             //   Fragment f=manager.findFragmentByTag("holder");
               Intent i=new Intent(Welcome.this,FullscreenActivity.class);
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(Welcome.this,iv,"zoom");
                i.putExtra("path",path);
                startActivity(i,options.toBundle());
            }
        });

        RecyclerView.ItemAnimator itemanimator = new DefaultItemAnimator();
        itemanimator.setAddDuration(400);
        itemanimator.setRemoveDuration(400);
        recyclerview.setItemAnimator(itemanimator);
        recyclerview.setSelected(true);

        //update cid value
       // _cid = statusData.getLatestTID();
        _cid = dbhandler.getLatestTID();
        editor = idpref.edit();
        editor.putInt("cid", 0);
        editor.commit();

        _id = idpref.getString("ID", "-1");
        _name = idpref.getString("name", "-1");
        _propic = idpref.getString("propic", "xyz");






     /*   profiilePicUpdaterThread=new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        profiilePicUpdaterThread.start();
        */
    }


    private void initialize() {
        idpref = getSharedPreferences(filename, 0);


        accountId.setText("ID  :  " + idpref.getString("ID", "-1"));
        accountName.setText(idpref.getString("name", "Anonymous").toUpperCase());

      //  etmessage = (EditText) findViewById(R.id.etMessage);
     //   bsend = (FloatingActionButton) findViewById(R.id.bsend);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerview = (RecyclerView) findViewById(R.id.mainbody);
        bsend.setOnClickListener(this);


        //hide the soft keyboard on startup
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etmessage.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);



        String propicPath = idpref.getString("propic", "xyz");
        String coverPicPath = idpref.getString("coverpic", "xyz");

        File file;
        file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), propicPath);
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(String.valueOf(file));
            propic.setImageBitmap(bmp);
        } else {
            propic.setImageResource(R.drawable.default_profile_pic);
        }
        file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), coverPicPath);
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(String.valueOf(file));
            coverPic.setImageBitmap(bmp);
        } else {
            coverPic.setImageResource(R.drawable.photodefault);
        }


    }


    //now this the handles the database listener updates
    private void getBroadcastChat(Bundle b1) {

        //Toast.makeText(getApplicationContext(),"Boraoadcast Received",Toast.LENGTH_LONG).show();

       // Bundle b1 = intent.getExtras();
        String uid = b1.getString("uid");
        String name = b1.getString("name");
        String mtype = b1.getString("mtype");
        String utype = b1.getString("utype");
        String propic = b1.getString("propic");
        String message = b1.getString("message");
        model = new ListModel(message, mtype, name, propic, uid, utype);
        if(lmanager.findLastVisibleItemPosition()==adapter.getItemCount()-1) {
            adapter.insert(adapter.getItemCount(), model);
            lmanager.scrollToPosition(adapter.getItemCount() - 1);
        }else{
            adapter.insert(adapter.getItemCount(), model);
        }
    }


    //initiaze chat
    public void getChat() {


        Cursor cursor;

        cursor = dbhandler.getStatusUpdate();
        startManagingCursor(cursor);
        if (cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(cursor.getColumnIndex("uid"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String mtype = cursor.getString(cursor.getColumnIndex("mtype"));
                String utype = cursor.getString(cursor.getColumnIndex("utype"));
                String propic = cursor.getString(cursor.getColumnIndex("propic"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                model = new ListModel(message, mtype, name, propic, uid, utype);
                row.add(model);
                //  fillView(uid,utype,mtype,message,name,propic);
            } while (cursor.moveToNext());
        }
        stopManagingCursor(cursor);
        cursor.close();

    }

//192.168.69.1

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.camera_action) {
//
//            handleCameraAction();
//
//        }
//        if(v.getId()==R.id.selectCover){
//            Intent cover = new Intent(Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            // Start the Intent
//            startActivityForResult(cover, 12345);
//        }
        if(v.getId()==R.id.selectNewProfilePic){

            Intent profile = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(profile, 22222);
        }
//
//        if (v.getId() == R.id.bsend) {
//            Toast.makeText(getApplicationContext(), etmessage.getText().toString(), Toast.LENGTH_SHORT).show();
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, CentralURL.MESSAGE_SENT_URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    if (response.contains("~")) {
//                        Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
//                        //  fillView(_id,"me","message",etmessage.getText().toString(),_name,_propic);
//                    }
//                    etmessage.setText("");
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> hashmap = new HashMap<String, String>();
//                    hashmap.put("id", _id);
//                    hashmap.put("message", etmessage.getText().toString());
//
//
//                    return hashmap;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    // do not add anything here
//                    return headers;
//                }
//            };
//            RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();
//
//
//            myQueue.add(stringRequest);
//
//         //   MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//
//        }


    }

    private void handleCameraAction() {

        // Handle the camera action
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File stoDir=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            File image=File.createTempFile(timestamp+"imageEnigma",".jpg",stoDir);

            fullSizeImagePath=image.getAbsolutePath();
            if(image!=null){
                Uri photoURI= FileProvider.getUriForFile(this,"com.example.shashank.enigmaproxy.fileprovider",image);
                i.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(i, 555);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==555) {
         //   Bundle extra = data.getExtras();
         //   bitmap = (Bitmap) extra.get("data");
            dialog=ProgressDialog.show(this, "Uploading", " Please Wait", false, false);
            saveToLocal();
           // File() accepts a full Uri too
        }
        if (requestCode==12345 && resultCode == RESULT_OK && null != data) {

            String fName="";
            // Get the Image from data
            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA ,MediaStore.Images.Media.TITLE};
//            // Get the cursor
//            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            // Move to first row
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String imgDecodableString = cursor.getString(columnIndex);
//            fName=cursor.getString(cursor.getColumnIndex(filePathColumn[1]))+".jpg";
//            cursor.close();
//            Bitmap cover=null;
//            cover= BitmapFactory
//                    .decodeFile(imgDecodableString);
            Bitmap cover=null;
            try {
                cover=MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Random x=new Random();
            x.setSeed(System.currentTimeMillis());
            fName=x.nextInt()+""+System.currentTimeMillis()+".jpg";
            if(cover!=null){

                coverPic.setImageBitmap(cover);
                File  file = new File (getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "enigma"+fName);
                if (file.exists ()){
                    Toast.makeText(getApplicationContext(),"File Exist",Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        cover.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"File Save Error",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                editor=idpref.edit();
                editor.putString("coverpic","enigma"+fName);
                editor.commit();
            }


        }
        if (requestCode == 22222 && resultCode == RESULT_OK  && null != data) {
            // Get the Image from data
            Uri selectedImage = data.getData();
           Toast.makeText(this, ""+selectedImage.toString(), Toast.LENGTH_SHORT).show();

            //this is for device which have default gallery and support com.android.camera.action.CROP
//            try {
//
//                Intent cropIntent = new Intent("com.android.camera.action.CROP");
//                // indicate image type and Uri
//                cropIntent.setDataAndType(selectedImage, "image/*");
//                // set crop properties
//                cropIntent.putExtra("crop", "true");
//                // indicate aspect of desired crop
//                cropIntent.putExtra("aspectX", 1);
//                cropIntent.putExtra("aspectY", 1);
//                // indicate output X and Y
//                cropIntent.putExtra("outputX", 200);
//                cropIntent.putExtra("outputY", 200);
//                // retrieve data on return
//                cropIntent.putExtra("return-data", true);
//                // start the activity - we handle returning in onActivityResult
//                startActivityForResult(cropIntent, 125);
//            }
//            // respond to users whose devices do not support the crop action
//            catch (ActivityNotFoundException e) {
//                Log.d(TAG, "onActivityResult: "+e.toString());
//                // display an error message
//                String errorMessage = "Whoops - your device doesn't support the crop action!";
//                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//                toast.show();
//            }



/*
                String[] filePathColumn = { MediaStore.Images.Media.DATA ,MediaStore.Images.Media.TITLE};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                fName=cursor.getString(cursor.getColumnIndex(filePathColumn[1]))+".jpg";
                cursor.close();

                    bitmap= BitmapFactory
                            .decodeFile(imgDecodableString);

                    ProfilePicHolder.setImageBitmap(bitmap);
*/

            //now we use the custopn cropping library
            CropImage.activity(selectedImage)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);



        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                    Random x=new Random();
                    x.setSeed(System.currentTimeMillis());
                    String fName=x.nextInt()+""+System.currentTimeMillis()+".jpg";
                    propic.setImageBitmap(bitmap);
                    saveToWeb(fName,bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }




    private String getMimeType(String path) {

        String ex= MimeTypeMap.getFileExtensionFromUrl(path);
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(ex);
    }


    public void saveToLocal() {

        final File f=new File(fullSizeImagePath);
        if(f.exists()){
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {

                    //to set the headers we need the extension info
                    String content_type=getMimeType(f.getPath());
                    //now we builsd the network request
                    //create the okhttp instance
                    OkHttpClient client=new OkHttpClient();
                    //then build the  file body
                    RequestBody fileBody=RequestBody.create(MediaType.parse(content_type),f);
                    Random r=new Random();
                    r.setSeed(System.currentTimeMillis());
                    String file_path=f.getAbsolutePath();
                    //then create the multipart request body
                    RequestBody requestBody=new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("id",idpref.getString("ID","0"))
                            .addFormDataPart("data",file_path.substring(file_path.lastIndexOf("/")+1),fileBody )
                            .build();

                    okhttp3.Request request=new okhttp3.Request.Builder()
                            .url(CentralURL.IMAGE_UPLOAD_URL)
                            .post(requestBody)
                            .build();
                    try {
                        okhttp3.Response responce=client.newCall(request).execute();
                        if(!responce.isSuccessful()){
                            Toast.makeText(Welcome.this, "Upload Error", Toast.LENGTH_SHORT).show();
                        }



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();

                }
            });
            t.start();
        }


    }






    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //add image resize logic here

        //
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imagebyte = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    protected void onResume() {
        super.onResume();
        idpref = getSharedPreferences(filename, 0);
        if(!idpref.getBoolean("PINENTERED",false)){
            startActivity(new Intent(Welcome.this,Login.class));
        }


        if (Updater.runFlag == false) {
            Log.d("SHASHANK", "SERVICE STARTED FROM onResume()");
            Intent backgroundService = new Intent(this, Updater.class);
            startService(backgroundService);
        }
        //register the broadcastreceiver
        ispaused = false;

//        try {
//            this.unregisterReceiver(receiver);
//            this.registerReceiver(receiver, filter);
//        } catch (Exception e) {
//
//        }

    /*
       THIS IS THE OLD METHOS FOR REFRESHING THE COVER AND PROFILE PIC

       idpref = getSharedPreferences(filename, 0);
        String propicPath = idpref.getString("propic", "xyz");
        String coverPicPath = idpref.getString("coverpic", "xyz");
      //  if(!propicPath.equals("xyz")){
        //    adapter.checkProfilePic(propicPath);
       // }
        File file;
        file = new File(getFilesDir(), propicPath);
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(String.valueOf(file));
            propic.setImageBitmap(bmp);
        } else {
            propic.setImageResource(R.drawable.default_profile_pic);
        }
        file = new File(getFilesDir(), coverPicPath);
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(String.valueOf(file));
            coverPic.setImageBitmap(bmp);
        } else {
            coverPic.setImageResource(R.drawable.photodefault);
        }
        */

    }

    @Override
    protected void onDestroy() {
        idpref = getSharedPreferences(filename, 0);
        editor=idpref.edit();
        editor.putBoolean("PINENTERED",false);
        editor.commit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ispaused = true;
        //it destroys this activity splash to clear memory
        // this.unregisterReceiver(receiver);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chatpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            editor = idpref.edit();
            editor.clear();
            editor.commit();
            finish();
            return true;
        }

        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.capture) {
            handleCameraAction();

        } else if (id == R.id.attachment) {
            Intent i = new Intent(Welcome.this, UploadImage.class);
            startActivityForResult(i, 999);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void saveToWeb(final String fName,final Bitmap bitmap) {


        dialog= ProgressDialog.show(Welcome.this,"Please Wait","Upload In Progess",false,false);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, CentralURL.PROFILE_PIC_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("SHASHANK TAG",response);

                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fName);
                if(file.exists()){
                    file.delete();
                }
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        SharedPreferences.Editor editor=idpref.edit();
                        editor.putString("propic",fName);
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error Saving the file",Toast.LENGTH_SHORT).show();
                    }




                dialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashmap=new HashMap<String,String>();
                hashmap.put("data",getStringImage(bitmap));
                hashmap.put("picname",fName);
                hashmap.put("id",idpref.getString("ID","0"));

                return hashmap;
            }
        };
        RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();


        myQueue.add(stringRequest);




    }


}
