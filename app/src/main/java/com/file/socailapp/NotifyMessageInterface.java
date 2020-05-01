package com.file.socailapp;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotifyMessageInterface
{
    @Headers({"Authorization:key=AAAAiJ74zn8:APA91bHzf6RiAh48Zpy6phBfGSrUSW8fayRfhLplPEsZLj97NpsOx3mk4E52jHLV1k9YKvg9saFZd85sazBybCtuaU3WRmrmWtavqMgfUtTdD3g2b7kwvprUaHZ5Krlre7QVr3-_BMxV","Content-type:application/json"})
    @POST("/fcm/send")
    Call<ResponseBody> sendNotifyMessage(@Body Map<String , Object> mapBody);
}
