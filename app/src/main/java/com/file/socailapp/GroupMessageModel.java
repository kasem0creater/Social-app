package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GroupMessageModel
{
    String sender;
    String message;
    String time;
    String type;
    String messageId;
    String groupId;


    public  GroupMessageModel()
    {}


    public GroupMessageModel(String sender, String message,  String time, String type, String messageId , String groupId) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.type = type;
        this.messageId = messageId;
        this.groupId = groupId;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }




    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }


    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getMessageId() {
        return messageId;
    }
    public String getGroupId() {
        return groupId;
    }


}
