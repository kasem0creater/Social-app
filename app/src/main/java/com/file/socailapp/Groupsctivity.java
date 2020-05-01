package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class Groupsctivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //recycle view
    private RecyclerView recyclerGroup;
    private GroupAdapter adapter;
    private List<GroupModel> models = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupsctivity);

        initialById();
    }

    private void initialById()
    {
        mAuth = FirebaseAuth.getInstance();

        //recycle
        recyclerGroup = findViewById(R.id.recycle_groups_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            loadGroupInfo();
        }
    }

    private void loadGroupInfo()
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = mAuth.getUid();
                models.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    if (data.child("participants").child(uid).exists())
                    {
                        //Log.i("load group chat",data.child("createBy").getValue().toString());
                        GroupModel model = data.getValue(GroupModel.class);

                        if (model != null)
                        {
                            Log.i("load group" , model.createBy);
                            models.add(model);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Log.i("load group chat","error");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
        settingRecycleView();
    }

    private void settingRecycleView()
    {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new GroupAdapter(getApplicationContext() , models);
        Log.i("group size",""+models.size());
        //
        recyclerGroup.setHasFixedSize(true);
        recyclerGroup.setLayoutManager(layoutManager);
        recyclerGroup.setAdapter(adapter);
    }
}
