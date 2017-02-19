package com.example.shashank.enigmaproxy;

import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Shashank on 22-12-2016.
 */

public class MultipartImageUploader {

    private Intent data=null;
    private String[] key=null;
    private String[] value=null;



    private OnUploadListener listener;

    private interface OnUploadListener{
        void onUploadStart();
        void onUploadFinish();
        void onError();
    }

    public void setOnUploadListener(OnUploadListener l){
        this.listener=l;
    }

    public MultipartImageUploader(){

    }
    public MultipartImageUploader(Intent i,String[] key,String[] value){
        this.data=i;
        this.key=key;
        this.value=value;
    }



    private String getMimeType(String path) {

        String ex= MimeTypeMap.getFileExtensionFromUrl(path);
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(ex);
    }



    public void uploadTask() {


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                //upload started
                if(listener!=null)
                listener.onUploadStart();
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
                RequestBody requestBody;
//                requestBody=new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("type",content_type)
//                        .addFormDataPart("data",file_path.substring(file_path.lastIndexOf("/")+1),fileBody )
//                        .addFormDataPart("id",idpref.getString("ID","0"))
//                        .build();

                MultipartBody.Builder builder=new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("data",file_path.substring(file_path.lastIndexOf("/")+1),fileBody );



                for(int i=0;i<key.length;i++){
                    builder.addFormDataPart(key[i],value[i]);
                }

                requestBody=builder.build();


                Request request=new Request.Builder()
                        .url(CentralURL.IMAGE_UPLOAD_URL)
                        .post(requestBody)
                        .build();
                try {
                    Response responce=client.newCall(request).execute();
                    if(!responce.isSuccessful()){
                        Log.d(TAG, "Upload Error Custom ");

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    //upload error occured
                    if(listener!=null)
                    listener.onError();
                }
                //upload finish
                if(listener!=null)
                    listener.onUploadFinish();
            }
        });

        t.start();


    }



}
