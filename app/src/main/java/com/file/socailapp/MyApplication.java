package com.file.socailapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        //enable firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //alter enable is , the data load show also offline
        Log.i("load data ","use offline Successful..");
    }
}
