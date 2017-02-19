package com.example.shashank.enigmaproxy;

/**
 * Created by Shashank on 29-06-2016.
 */
public class ListModel {

    public String uid,utype,mtype,message,name,propic;

    public ListModel(String message, String mtype, String name, String propic, String uid, String utype) {
        this.message = message;
        this.mtype = mtype;
        this.name = name;
        this.propic = propic;
        this.uid = uid;
        this.utype = utype;
    }

}
