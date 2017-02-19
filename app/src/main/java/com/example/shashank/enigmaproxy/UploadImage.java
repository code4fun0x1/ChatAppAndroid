package com.example.shashank.enigmaproxy;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.shashank.enigmaproxy.R.id.imageView;


public class UploadImage extends Activity implements View.OnClickListener{

    ImageView holder;
    FloatingActionButton upload;
    ProgressBar pBar;
    Bitmap bitmap=null;
    private int PICK_IMAGE_REQUEST=1;
    SharedPreferences idpref;
    private static String filename="USERID";
    String imgDecodableString;
    ProgressDialog loading;
    String imgActualPath;
    public static final String TAG="ImageUpload";
    Uri selectedImage=null;
    Intent data=null;
    long totalSize=0;
    FragmentManager manager;
    ProgressFragment pFragment;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload);
        idpref=getSharedPreferences(filename,0);
        initialize();

        mHandler= new Handler();
        new MaterialFilePicker()
                .withActivity(UploadImage.this)
                .withRequestCode(PICK_IMAGE_REQUEST)
                .start();

        manager=getFragmentManager();
        pFragment=new ProgressFragment();
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void initialize() {
        //bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.mainicon);
        holder=(ImageView)findViewById(imageView);
        //choose=(Button)findViewById(R.id.chooseButton);
        upload=(FloatingActionButton) findViewById(R.id.uploadButton);
        pBar=(ProgressBar)findViewById(R.id.progressBar3);
        //choose.setOnClickListener(this);
        upload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.uploadButton:

                //for upload we have to convert bitmap to string

                if(data!=null){
                    //loading = ProgressDialog.show(this, "Uploading", " Please Wait", false, false);
                    //loading.setMax(100);
                   // pFragment.show(manager,"PROGRESS");
                    pBar.setVisibility(View.VISIBLE);
                    saveToLocal();


                }else{
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    new MaterialFilePicker()
                            .withActivity(UploadImage.this)
                            .withRequestCode(PICK_IMAGE_REQUEST)
                            .start();
                }


                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                this.data=data;

                bitmap= BitmapFactory.decodeFile(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                holder.setImageBitmap(bitmap);

            }else{
                Toast.makeText(this, "Empty Data From onActivityResult", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d(TAG, "ImageUpload  "+e.toString());
        }
    }

    private String getMimeType(String path) {

        String ex= MimeTypeMap.getFileExtensionFromUrl(path);
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(ex);
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        String realPath=null;
        try {
          // String[] proj = { MediaStore.Images.Media.DATA };
            String[] proj = { MediaStore.Images.ImageColumns.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);


           // int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            cursor.moveToFirst();
            realPath= cursor.getString(column_index);
        }catch (Exception e){
            Log.d(TAG, "getRealPathFromURI: "+e.toString());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return realPath;
    }


//    public String getPath(Uri uri) throws Exception{
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        Log.d(TAG, "getPath: "+document_id);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//
//        return path;
//    }



//now it uploads to web
    public void saveToLocal() {


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                File f=new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
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

                //here we pass the orginal request body to custom one
                CustomRequestBody customRequestBody=new CustomRequestBody(requestBody, new CustomRequestBody.Listener() {
                    @Override
                    public void onProgress(final int progress) {

                        Log.d(TAG, "UPLOAD PROGRESS "+progress);
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                pFragment.updateProgressbar(progress);
//                            }
//                        });
                        pBar.setProgress(progress);

                    }
                });

                Request request=new Request.Builder()
                        .url(CentralURL.IMAGE_UPLOAD_URL)
                        .post(customRequestBody)
                        .build();
                try {
                    Response responce=client.newCall(request).execute();
                    if(!responce.isSuccessful()){
                        Log.d(TAG, "Upload Error Custom ");

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
                //loading.dismiss();
                //pFragment.dismiss();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });
        t.start();




//        //Uploading code
//        try {
//            String uploadId = UUID.randomUUID().toString();
//            //Creating a multi part request
//            new MultipartUploadRequest(this, uploadId, CentralURL.IMAGE_UPLOAD_URL)
//                    .addFileToUpload(imgActualPath, "data") //Adding file
//                    .addParameter("id",idpref.getString("ID","0")) //Adding text parameter to the request
//                    .setNotificationConfig(new UploadNotificationConfig())
//                    .setMaxRetries(2)
//                    .startUpload(); //Starting the upload
//
//        } catch (Exception exc) {
//            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
//            loading.dismiss();
//        }


//
//        StringRequest stringRequest=new StringRequest(Request.Method.POST, CentralURL.IMAGE_UPLOAD_URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                Log.d("SHASHANK TAG",response);
//                loading.dismiss();
//                finish();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loading.dismiss();
//                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String,String> hashmap=new HashMap<String,String>();
//                hashmap.put("data",getStringImage(bitmap));
//                hashmap.put("id",idpref.getString("ID","0"));
//
//                return hashmap;
//            }
//        };
//        RequestQueue myQueue= MySingleton.getInstance(getApplicationContext()).getRequestQueue();
//       myQueue.add(stringRequest);


    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //add image resize logic here

        //
        bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imagebyte=baos.toByteArray();
        String encodedImage= Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encodedImage;
    }


    //uploader asynctask





}
