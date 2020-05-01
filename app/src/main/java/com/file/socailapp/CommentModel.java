package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CommentModel
{

    private String commentId;
    private String date;
    private String time;
    private String image;
    private String message;
    private String name;
    private String uid;
    private String postId;


    public CommentModel()
    {}

    public CommentModel(String commentId, String date, String time, String image, String message, String name, String uid, String postId) {
        this.commentId = commentId;
        this.date = date;
        this.time = time;
        this.image = image;
        this.message = message;
        this.name = name;
        this.uid = uid;
        this.postId = postId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }




    public String getCommentId() {
        return commentId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPostId() {
        return postId;
    }



}
