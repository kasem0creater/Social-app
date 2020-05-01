package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class PostModel
{
    private String uid;
    private String name;
    private String body;
    private String image;
    private String status_post;
    private String date;
    private String time;
    private String like;
    private String postId;
    private String totalComment;
    //private List<CommentModel> comment;


    public PostModel(){}


    public PostModel(String uid, String name, String body, String image, String status_post, String date, String time, String like,String postId ,String total_comment) {
        this.uid = uid;
        this.name = name;
        this.body = body;
        this.image = image;
        this.status_post = status_post;
        this.date = date;
        this.time = time;
        this.like = like;
        this.postId = postId;
        this.totalComment = total_comment;
    }



    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus_post(String status_post) {
        this.status_post = status_post;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLike(String like) {
        this.like = like;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setTotalComment(String comment) {
        this.totalComment = comment;
    }



    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }

    public String getStatus_post() {
        return status_post;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLike() {
        return like;
    }
    public String getPostId() {
        return postId;
    }
    public String getTotalComment() {
        return totalComment;
    }


}
