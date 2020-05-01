package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MessageEntity
{
    private String from;
    private String to;
    private String type;
    private String message;
    private String time;
    private String date;
    private String messageId;


    public MessageEntity()
    {}

    public MessageEntity(String from, String to, String type, String message, String time, String date ,String messageId) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.message = message;
        this.time = time;
        this.date = date;
        this.messageId = messageId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }



    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
    public String getMessageId() {
        return messageId;
    }



}
