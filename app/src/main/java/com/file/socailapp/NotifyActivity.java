package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotifyActivity extends AppCompatActivity {

    //firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    //recycle view
    private RecyclerView recyclerNotify;
    private NotifyAdapter adapter;

    private List<NotifyModel> models = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        initialById();
    }

    private void initialById()
    {
        //recycle
        recyclerNotify = findViewById(R.id.recycle_notify);

        //firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            loadNotify();
        }
    }

    private void loadNotify()
    {
        mRef.child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    NotifyModel  model = data.getValue(NotifyModel.class);

                    if (model != null)
                    {
                        String current = mAuth.getUid();

                       //check why post
                        if (!model.uid.equals(current))
                        {
                            //todo send notify to all friends not current user
                            models.add(model);
                            adapter.notifyDataSetChanged();
                        }
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
        adapter = new NotifyAdapter(getApplicationContext() , models);

        recyclerNotify.setHasFixedSize(true);
        recyclerNotify.setLayoutManager(layoutManager);
        recyclerNotify.setAdapter(adapter);
    }
}
