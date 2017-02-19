package com.example.shashank.enigmaproxy.firebaseimplementation;

/**
 * Created by Shashank on 20-01-2017.
 */

public class ChatModel {


    private String uid;
    private String message;
    private String mtype;

    public ChatModel(String message, String mtype, String uid) {
        this.message = message;
        this.mtype = mtype;
        this.uid = uid;
    }

    public ChatModel() {

    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
