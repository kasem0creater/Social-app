package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class NotifyModel
{
    String uid;
    String postId;
    String message;
    String date;
    String time;


    public  NotifyModel()
    {}

    public NotifyModel(String uid, String postId, String message,  String date ,String time) {
        this.uid = uid;
        this.postId = postId;
        this.message = message;
        this.date = date;
        this.time =time;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }




    public String getUid() {
        return uid;
    }

    public String getPostId() {
        return postId;
    }

    public String getMessage() {
        return message;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }


}
