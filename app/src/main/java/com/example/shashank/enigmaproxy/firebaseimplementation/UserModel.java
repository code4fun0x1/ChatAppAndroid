package com.example.shashank.enigmaproxy.firebaseimplementation;

/**
 * Created by Shashank on 15-03-2017.
 */

public class UserModel {

    private String name,uid,propic,email;

    public UserModel() {
    }



    public UserModel(String name, String uid, String propic, String email) {
        this.name = name;
        this.uid = uid;
        this.propic = propic;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
