package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GroupDetailActivity extends AppCompatActivity {

    //get string extra
    private String groupId;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,mRefLeaveGroup;

    //text view
    private TextView txtGroupTitle , txtGroupDescrip,txtTotalUser;

    //button
    private Button btnAddFriend , btnLeaveGroup;

    //image
    CircularImageView imageGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);


        initialById();

        //
        getStringExtra();

        addFriendGroup(groupId);

        //
        leaveGroup(groupId);
    }

    private void leaveGroup(String groupId)
    {
        mRefLeaveGroup = FirebaseDatabase.getInstance().getReference().child("Groups");
        String uid = mAuth.getUid().toString();

        btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRefLeaveGroup.child(groupId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("participants").child(uid).exists())
                        {
                            mRefLeaveGroup.child(groupId).child("participants").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext() ,"Leave Group Successful..",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    //add friend to group your chat
    private void addFriendGroup(String groupId)
    {
      btnAddFriend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(getApplicationContext() , AddFriendToGroupActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.putExtra("groupId",groupId);
              startActivity(intent);
          }
      });
    }

    private void initialById()
    {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        //text view
        txtGroupTitle = findViewById(R.id.txt_group_title_detail);
        txtGroupDescrip = findViewById(R.id.txt_group_descrip_detail);
        txtTotalUser = findViewById(R.id.txt_total_user_group);

        //btn
        btnAddFriend = findViewById(R.id.btn_add_friend_group);
        btnLeaveGroup = findViewById(R.id.btn_leave_group);

        //image
        imageGroup = findViewById(R.id.image_group_detail);
    }

    private void getStringExtra()
    {
        groupId = getIntent().getStringExtra("groupId");
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            //
            loadGroupInfo();
        }
    }

    private void loadGroupInfo()
    {
        mRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {

                    GroupModel model = dataSnapshot.getValue(GroupModel.class);

                    String image = model.getGroupIcon();

                    if (!image.isEmpty())
                    {
                        Picasso.get().load(image).error(R.drawable.icon_profile).into(imageGroup);
                    }

                    //set text to view
                    txtGroupTitle.setText(model.getGroupTitle());
                    txtGroupDescrip.setText(model.getGroupDescription());

                }

                //load total user on group
                mRef.child(groupId).child("participants").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int countTotalUser = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            if (data != null)
                            {
                               countTotalUser+=1;
                            }
                        }
                        txtTotalUser.setText(countTotalUser+" users");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
