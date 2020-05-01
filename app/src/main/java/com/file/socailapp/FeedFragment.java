package com.file.socailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mRefPost , mRefKey;

    //recycle view
    private RecyclerView recyclerPost;
    private List<PostModel> models = new ArrayList<>();


    private PostAdater  adater;

    public static FeedFragment newInstance() {

        Bundle args = new Bundle();

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        recyclerPost = view.findViewById(R.id.recycle_post_list);

        return view;
    }


    @Override
    public void onStart() {
        if (mAuth.getCurrentUser() != null)
        {
            writeOnlineStatus("online");
            models.clear();
            loadPost();
        }
        super.onStart();

    }


    private void loadPost()
    {
        mRefKey = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");

        mRefKey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    //  Log.i("load post",name +":"+body );
                    PostModel postModel =  data.getValue(PostModel.class);
                    //Log.i("load post",postModel.getBody()+"");
                    if (postModel != null)
                    {
                        models.add(postModel);
                    }
                }
                Collections.reverse(models);
                adater.notifyDataSetChanged();
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
        //adater = new PostAdater(this.getView().getContext().getApplicationContext(),models);
        //layout
        adater = new PostAdater(getContext().getApplicationContext(),models);
        adater.notifyDataSetChanged();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getView().getContext());

        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(layoutManager);
        recyclerPost.setAdapter(adater);
    }

    //write user online status and offline
    private void writeOnlineStatus(String status)
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(mAuth.getUid()).child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
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

}
