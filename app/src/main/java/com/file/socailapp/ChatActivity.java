package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    //toobar
    private Toolbar toolbar;

    //text view
    private TextView txtName;

    //edit text
    private EditText txtMessage;

    //image
    private CircularImageView imageUser,userOnlineImage;

    //string
    private String uid , userName,image , statusOnline;

    //button
    private ImageButton btnSendMessage;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mWriteuser,mRefdevice;

    //recycle view
    private RecyclerView recyclerView;
    private AdaterReceiverMessage adater;

    //list message
    private List<MessageEntity> listMessage = new ArrayList<>();
    private List<String> listKey = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         uid = getIntent().getStringExtra("uid");
         userName = getIntent().getStringExtra("userName");
         image = getIntent().getStringExtra("image");
         statusOnline = getIntent().getStringExtra("status");

        initialById();

        settingAppBar();

        //send message
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


    }

    @Override
    protected void onStart() {

      if (mAuth.getCurrentUser() != null)
      {
          //
          writeOnlineStatus("online");
      }

        super.onStart();

        //get message history
        //
        listMessage.clear();
        mRef.child("Messages").child(uid).child(mAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                   String from = dataSnapshot.child("from").getValue().toString();
                   String to = dataSnapshot.child("to").getValue().toString();
                   String type = dataSnapshot.child("type").getValue().toString();
                   String message = dataSnapshot.child("message").getValue().toString();
                   String time = dataSnapshot.child("time").getValue().toString();
                   String date = dataSnapshot.child("date").getValue().toString();
                   String key = dataSnapshot.getRef().getKey();

                   Log.i("load chat",key);
                   MessageEntity entity = new MessageEntity(from,to,type,message,time,date,key);

                   if (entity != null && key != null)
                   {
                       listMessage.add(entity);
                       listKey.add(key);
                   }
                    adater.notifyDataSetChanged();
                   recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                Log.i("get message","");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.i("get message",""+listMessage.get(1).getMessage());
        settingRecycleView();

    }



    private void initialById()
    {
        toolbar = findViewById(R.id.app_bar_message_of_user);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        //button
        btnSendMessage = findViewById(R.id.btn_sendMessage);

        //Edit text
        txtMessage = findViewById(R.id.txt_messages);

        //recycle
        recyclerView = findViewById(R.id.recycle_message_list_of_user);
    }


    private void settingRecycleView()
    {
        adater = new AdaterReceiverMessage(listMessage,listKey);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adater);
    }

    private void settingAppBar()
    {
       // toolbar.setTitle("");
        setSupportActionBar(toolbar);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(view);

        txtName = (TextView) findViewById(R.id.txt_user_name_friend);
        imageUser = (CircularImageView) findViewById(R.id.image_profile_chat);
        userOnlineImage = (CircularImageView) findViewById(R.id.image_status_chat);


        txtName.setText(userName);
        if(!image.isEmpty())
            Picasso.get().load(image).into(imageUser);

        if(statusOnline.equals("online"))
        {
            userOnlineImage.setVisibility(View.VISIBLE);
        }
    }


    /*
    user send message
     */
    private void sendMessage()
    {
        String message = txtMessage.getText().toString();
        if(!TextUtils.isEmpty(message))
        {
            Calendar calendar =  Calendar.getInstance();

            SimpleDateFormat stime = new SimpleDateFormat("HH:mm:ss a");
            SimpleDateFormat sdate = new SimpleDateFormat("dd-MM-yyyy ");

            String time = stime.format(calendar.getTime());
            String date = sdate.format(calendar.getTime());
            String key = mRef.push().getKey();

            String sender = "Messages/"+mAuth.getUid().toString()+"/"+uid;
            String friend = "Messages/"+uid+"/"+mAuth.getUid().toString();


            Map<String,Object> mapBody = new HashMap<>();
            mapBody.put("from",mAuth.getUid());
            mapBody.put("type","text");
            mapBody.put("message",message);
            mapBody.put("to",uid);
            mapBody.put("time",time);
            mapBody.put("date",date);
            mapBody.put("messageId",key);


            Map<String,Object> mapDetail = new HashMap<>();
            mapDetail.put(sender+"/"+key,mapBody);
            mapDetail.put(friend+"/"+key,mapBody);

            mRef.updateChildren(mapDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                       mRefdevice = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
                       mRefdevice.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               String deivceToken = dataSnapshot.child("DeviceToken").getValue().toString();

                               sendNotify("Message",message,deivceToken);
                               Toast.makeText(getApplicationContext() ,"send",Toast.LENGTH_LONG).show();
                               txtMessage.setText("");
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                    }
                }
            });

        }
        else
        {
            //todo text empty
        }
    }


    //write user online status and offline
    private void writeOnlineStatus(String status)
    {
        mWriteuser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mWriteuser.child(mAuth.getUid()).child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.i("write status","Successful...");
                }
                else
                {
                    Log.w("write status","Error...");
                }
            }
        });
    }

    //send notify use retrofit
    private void sendNotify(String title , String message,String token)
    {
        //token

        //sound
        Uri ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //setting map

        Map<String,Object> mapBody = new HashMap<>();
        mapBody.put("subtitle",title+"");
        mapBody.put("message",message+"");
//        mapBody.put("sound",ringtoneManager+"");
//        mapBody.put("icon","icon_chat");
        mapBody.put("notifyType","chat");
        mapBody.put("to",uid);

        Map<String,Object> mapDetail = new HashMap();
        mapDetail.put("to",token);
        mapDetail.put("data",mapBody);

        NotifyMessageAPI messageAPI = new NotifyMessageAPI();
        Call<ResponseBody> call =  messageAPI.getApi().sendNotifyMessage(mapDetail);

        //
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    Log.i("send notify message","Successful.. :");
                }
                else
                {
                    Log.w("send notify message","Error :"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.w("send notify message","Error :"+t.getMessage());
            }
        });
    }

}
