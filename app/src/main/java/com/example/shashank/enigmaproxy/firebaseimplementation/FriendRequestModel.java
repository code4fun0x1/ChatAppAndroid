package com.example.shashank.enigmaproxy.firebaseimplementation;

/**
 * Created by Shashank on 06-03-2017.
 */

public class FriendRequestModel {

    private String uid;
    private String name;
    private String message;

    public FriendRequestModel() {

    }

    public FriendRequestModel(String uid, String name, String message) {
        this.uid = uid;
        this.name = name;
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
