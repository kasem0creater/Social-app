package com.file.socailapp;

public class Participants
{
    String uid;

    public Participants(String uid, String role) {
        this.uid = uid;
        this.role = role;
    }

    String role ;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

}
