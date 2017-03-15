package com.example.shashank.enigmaproxy.firebaseimplementation;

/**
 * Created by Shashank on 15-03-2017.
 */

public class RequestModel {

    private String name,uid,propic;

    public RequestModel() {
    }

    public RequestModel(String name, String uid, String propic) {
        this.name = name;
        this.uid = uid;
        this.propic = propic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }
}
