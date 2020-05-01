package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private String  groupId;

    //firebase
    private DatabaseReference mRef,mRefGroupMessage,mRefLoadMessage;
    private FirebaseAuth mAuth;
    private StorageReference sRef;
    
    //recycle view
    private RecyclerView recyclerGroupChat;
    private List<GroupMessageModel> models = new ArrayList<>();
    private GroupMessageAdapter adapter;

    //toolbar
    private Toolbar toolbar;

    //text
    private TextView txtGroupName;
    private CircularImageView imageGroup;

    //message
    private ImageButton btnImage,btnSendMessage,btnGroupInfo;
    private EditText txtMessage;

    //image
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupId = getIntent().getStringExtra("groupId");
        
        //
        initialById();

        //
        settingAppBar();

        //send message
        sendMessage(groupId);

        //open image
        pickImageGallery();

        //btn group info click
        btnGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTogroupDetail(groupId);
            }
        });
    }

    private void sendTogroupDetail(String groupId)
    {
        Intent intent = new Intent(getApplicationContext() ,GroupDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupId",groupId);
        startActivity(intent);
    }

    private void initialById()
    {
        //firebase
        mAuth = FirebaseAuth.getInstance();

        //tool bar

        //image btn
        btnImage = findViewById(R.id.btn_send_image);
        btnSendMessage = findViewById(R.id.btn_sendMessage);

        //text
        txtMessage = findViewById(R.id.txt_messages);

        //recycle view
        recyclerGroupChat = findViewById(R.id.recycle_group_chat);
    }

    private void settingAppBar()
    {
        toolbar =(Toolbar) findViewById(R.id.app_bar_group_);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  view = layoutInflater.inflate(R.layout.custom_group_app_bar , null);

        actionBar.setCustomView(view);

        //by id and set view

        txtGroupName = findViewById(R.id.txt_group_name);
        imageGroup = findViewById(R.id.image_group_icon);
        btnGroupInfo = findViewById(R.id.btn_group_info);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            loadGroupInfo(groupId);

            loadMessage(groupId);
        }
    }

    private void loadMessage(String groupId)
    {
        models.clear();
        mRefLoadMessage = FirebaseDatabase.getInstance().getReference().child("Groups");
        mRefLoadMessage.child(groupId).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    GroupMessageModel model = dataSnapshot.getValue(GroupMessageModel.class);

                    // String message = dataSnapshot.child("message").getValue().toString();


                    if (model != null)
                    {
                        Log.i("load group chat", model.getType());
                      //  txtGroupName.setText(model.getType());
                        models.add(model);
                        adapter.notifyDataSetChanged();
                        recyclerGroupChat.smoothScrollToPosition(adapter.getItemCount());
                    }
                }
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

        //setting recycle view
        settingRecycleView();
    }

    private void settingRecycleView()
    {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new GroupMessageAdapter(getApplicationContext() , models);

        //
        recyclerGroupChat.setHasFixedSize(true);
        recyclerGroupChat.setLayoutManager(layoutManager);
        recyclerGroupChat.setAdapter(adapter);
    }

    private void loadGroupInfo(String groupId)
    {
     mRef = FirebaseDatabase.getInstance().getReference().child("Groups");
     mRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String title = dataSnapshot.child("groupTitle").getValue().toString();
                 String descrip = dataSnapshot.child("groupDescription").getValue().toString();
                 String icon =  dataSnapshot.child("groupIcon").getValue().toString();

                 Log.i("load group info",title+descrip);
                 if (!icon.isEmpty())
                 {
                     Picasso.get().load(icon).error(R.drawable.icon_profile).into(imageGroup);
                 }

                 txtGroupName.setText(title);

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });
    }

    private void pickImageGallery()
    {
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
        }
    }

    private void sendMessage(String groupId)
    {
       btnSendMessage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mRefGroupMessage = FirebaseDatabase.getInstance().getReference().child("Groups");
               sRef = FirebaseStorage.getInstance().getReference().child("Group Message").child("Images");
               String time = System.currentTimeMillis()+"";
               String key = mRefGroupMessage.push().getKey();


               String path = time+"\n"+key+".jpg";

               if (imageUri != null)
               {
                   //send message to group type image
                   sRef.child(path).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if (task.isSuccessful())
                           {
                               task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Uri> task) {
                                       if (task.isSuccessful())
                                       {

                                           //save message body to db
                                           Map<String , Object> map = new HashMap<>();
                                           map.put("sender",mAuth.getUid().toString());
                                           map.put("message",task.getResult().toString()+"");
                                           map.put("time",time);
                                           map.put("type","image");
                                           map.put("messageId",key);
                                           map.put("groupId",groupId);

                                           mRefGroupMessage.child(groupId).child("message").child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful())
                                                   {
                                                       txtMessage.setText("");
                                                   }
                                               }
                                           });

                                           //
                                       }
                                   }
                               });
                           }
                       }
                   });
               }
               else
               {
                   //send message group text
                   String message =  txtMessage.getText().toString();

                   Map<String , Object> map = new HashMap<>();
                   map.put("sender",mAuth.getUid().toString());
                   map.put("message",message+"");
                   map.put("time",time);
                   map.put("type","text");
                   map.put("messageId",key);
                   map.put("groupId",groupId);

                   mRefGroupMessage.child(groupId).child("message").child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful())
                           {
                               txtMessage.setText("");
                           }
                       }
                   });
               }
           }
       });
    }
}
