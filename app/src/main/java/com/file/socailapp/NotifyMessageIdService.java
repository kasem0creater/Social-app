package com.file.socailapp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotifyMessageIdService extends FirebaseMessagingService
{

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
                super.onMessageReceived(remoteMessage);

        Log.i("send notify","Working");
        String notifyType;
        String message;
        String title;
        String uid;
        String postId;

        //check notify type chat or image
        Map<String,String> data = remoteMessage.getData();

         notifyType = data.get("notifyType");
         message = data.get("message");
         title = data.get("subtitle");
         uid = data.get("to");
         postId = data.get("postId");

        if (notifyType.equals("chat"))
        {
            //chat notify

            sendNotify(message,title,uid);
            Log.i("send notify","Chat :"+remoteMessage.getData().get("message"));
        }
        else if (notifyType.equals("post"))
        {
            mAuth = FirebaseAuth.getInstance();

            String current = mAuth.getUid();

           if (!uid.equals(current))
           {
               //post notify
               sendNotifyPost(message , title , uid,postId);
               Log.i("send notify","Post :"+remoteMessage.getData().get("message"));
           }
        }
    }

    private void sendNotifyPost(String message, String title, String uid , String posId)
    {
        //setting intent
        Intent intent = new Intent(this , PostdetailActivity.class);
        intent.putExtra("postId",posId);
        intent.putExtra("uidPost",uid);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this , 1 ,intent ,PendingIntent.FLAG_ONE_SHOT);
        String chanelId ="PostNotify";

        Uri ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder  builder = new NotificationCompat.Builder(this , chanelId);
        builder.setSmallIcon(R.drawable.icon_chat);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSound(ringtoneManager);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        //
        NotificationManager  manager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(chanelId ,"post notify",NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(1,builder.build());
        Log.i("send notify","successful..");
    }

    private void sendNotify(String message , String title,String uid)
    {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                .child("UserInfo/Data");
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();

                intentChatNotify(name , image , uid , status , title , message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void intentChatNotify(String name, String image, String uid, String status,String title , String message)
    {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("userName",name);
        intent.putExtra("image",image);
        intent.putExtra("uid",uid+"");
        intent.putExtra("status",status);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 , intent ,PendingIntent.FLAG_ONE_SHOT);
        String chanelNotify = "messageNotify";
        Uri ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //build notify
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,chanelNotify);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSound(ringtoneManager);
        builder.setSmallIcon(R.drawable.icon_chat);
        builder.setAutoCancel(true);
        //builder.setLo
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(chanelNotify,"message notify",NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

        }
        manager.notify(0,builder.build());
        Log.i("send notify","successful..");

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
            mRef.child(mAuth.getUid()).child("DeviceToken").setValue(s+"")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Log.i("refresh token","Successful..");
                            }
                        }
                    });
        }

    }
}
