package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendToGroupActivity extends AppCompatActivity {


    //get string extra
    private static String groupId;

    //adapter recycle
    private AddFriendGroupAdapter adapter;

    //list
    private List<UserInfo> models = new ArrayList<>();

    private RecyclerView recyclerView;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    //toolbar
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_group);

        getStringExtra();

        initialById();

        settingAppBar();
    }

    private void initialById()
    {
        //recycle view
        recyclerView = findViewById(R.id.recycle_show_friend_add_group);

        //toolbar
        toolbar = findViewById(R.id.app_bar_friend_group);

        //firebase
        mAuth =FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
    }

    private void getStringExtra()
    {
        groupId = getIntent().getStringExtra("groupId");
    }

    private void settingAppBar()
    {
        getSupportActionBar();
        setSupportActionBar(toolbar);
    }


    //start app and load all user info from db firebase

    @Override
    protected void onStart() {
        super.onStart();

        //check user login on app
        if (mAuth.getCurrentUser() != null)
        {

            //load all user
            loadAllUsers();
        }
    }

    private void loadAllUsers()
    {
        String uid = mAuth.getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    UserInfo info = data.getValue(UserInfo.class);

                    if (info != null)
                    {
                        /// not show current user this in list
                        if (!info.getUid().equals(uid))
                        {
                            models.add(info);
                        }
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
        adapter = new AddFriendGroupAdapter(getApplicationContext() , models,"user" ,groupId);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        //rec
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //create menu option


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_find_friends_group , menu);
        MenuItem item = menu.findItem(R.id.menu_finds_friends_group);
        SearchView  searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (!TextUtils.isEmpty(query.trim()))
                {
                    Log.i(" <search","working...");
                    searchUser(query);
                }
                else
                {
                    loadAllUsers();
                    Log.i(" <search","not working...");
                }
                return false;
            }
        });

        //click
        return true;
    }

    private void searchUser(String query)
    {
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    UserInfo info = data.getValue(UserInfo.class);

                    if(info != null)
                    {
                        if (info.getName().toLowerCase().equals(query.toLowerCase()))
                        {
                            Log.i("get all user","search...");
                            models.add(info);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settingRecycleView();
    }


}
