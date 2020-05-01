package com.file.socailapp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserInfo
{
    String  Email;
    String Name;
    String NickName;
    String Image;
    String Uid;
    String Status;
    String DeviceToken;

    public UserInfo(){}

    public UserInfo(String email, String name, String nickName, String image, String uid, String status,String deviceToken) {
        Email = email;
        Name = name;
        NickName = nickName;
        Image = image;
        Uid = uid;
        this.Status = status;
        this.DeviceToken = deviceToken;
    }


    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
    public void setToken(String token)
    {
        this.DeviceToken = token;
    }


    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getNickName() {
        return NickName;
    }

    public String getImage() {
        return Image;
    }

    public String getUid() {
        return Uid;
    }

    public String getStatus() {
        return Status;
    }
    public String getToken(){return DeviceToken;}




    //to map
    @Exclude
    public Map<String , Object> toMap()
    {
        Map<String , Object> map = new HashMap<>();
        map.put("Email",Email);
        map.put("Name",Name);
        map.put("NickName",NickName);
        map.put("Image",Image);
        map.put("Uid",Uid);
        map.put("Status",Status);
        map.put("DeviceToken",DeviceToken);
        return map;
    }
}
