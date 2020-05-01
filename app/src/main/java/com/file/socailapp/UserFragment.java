package com.file.socailapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private RecyclerView recyclerView;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //adapter recycle
    private AllUserAdapter allUserAdapter;

    //list
    private List<UserInfo> info = new ArrayList<>();

    public static UserFragment newInstance() {

        Bundle args = new Bundle();

        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        initialById(view);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        if (mAuth.getCurrentUser() != null)
        {
            writeOnlineStatus("online");
            getDataToadapter();
        }
        super.onStart();
    }


    private void initialById(View view)
    {
        recyclerView = view.findViewById(R.id.recycle_show_all_user);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
    }



    private void getDataToadapter()
    {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.clear();
               for (DataSnapshot data : dataSnapshot.getChildren()) {
                   UserInfo i  = data.getValue(UserInfo.class);

                   if(i != null)
                   {
                         String uid = mAuth.getUid();
                         if (!i.getUid().equals(uid))
                         {
                             info.add(i);
                         }
                   }
                   allUserAdapter.notifyDataSetChanged();
               }

                Log.i("get all user","Successful..."+info.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        settingRecycle();
    }

    private void settingRecycle()
    {
        allUserAdapter = new AllUserAdapter(getView().getContext().getApplicationContext(),info,"user");

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(allUserAdapter);
       // allUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
       //inflater.inflate(R.menu.menu_main_setting , menu);

        MenuItem item = menu.findItem(R.id.menu_search_user);
        MenuItem postIcon = menu.findItem(R.id.menu_post);

        item.setVisible(true);
        postIcon.setVisible(false);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim()))
                {
                    searchUser(newText);
                }
                else
                {
                    getDataToadapter();
                }
                return false;
            }
        });
    }

    private void searchUser(String query)
    {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UserInfo i  = data.getValue(UserInfo.class);

                    if(i != null)
                    {
                        if (i.getName().toLowerCase().equals(query.toLowerCase()))
                        {
                            info.add(i);
                            allUserAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        settingRecycle();
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
