package com.file.socailapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotifyMessageAPI
{
    private static String baseUrl ="https://fcm.googleapis.com/";
    private static Retrofit retrofit = null;
    private NotifyMessageInterface api;

    public NotifyMessageInterface getApi()
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        }
        api =retrofit.create(NotifyMessageInterface.class);
        return api;
    }
}
