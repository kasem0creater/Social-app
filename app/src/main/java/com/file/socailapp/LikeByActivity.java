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

public class LikeByActivity extends AppCompatActivity {


    //recycle
    private RecyclerView recyclerLike;
    private WhoLikeAdapter adapter;
    private List<LikeModel> models = new ArrayList<>();

    //Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    //string resource
    private String uid;
    private String postId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_by);

        initialById();

        getStringExtra();
    }

    private void getStringExtra()
    {
        uid = getIntent().getStringExtra("uid");
        postId = getIntent().getStringExtra("posId");
    }

    //find bi id view
    private void initialById()
    {
        recyclerLike = findViewById(R.id.recycle_who_like);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            loadUserLike();
        }

    }

    //load info , who like this post
    private void loadUserLike()
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        mRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String key = data.getRef().getKey();

                    LikeModel model = new LikeModel(key);

                    if (model != null)
                    {
                        Log.i("load who like",model.getUid());
                        models.add(model);
                    }
                    adapter.notifyDataSetChanged();
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
        adapter = new WhoLikeAdapter(getApplicationContext(),models , "Like");

        recyclerLike.setHasFixedSize(true);
        recyclerLike.setLayoutManager(layoutManager);
        recyclerLike.setAdapter(adapter);
    }
}
