package com.file.socailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRefchat , mRefUser;

    //recycle view
    private RecyclerView recyclerChatList;
    private List<UserInfo> infoList = new ArrayList<>();
    private AllUserAdapter adapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //initial id view
        initialById(view);


        return view;
    }

    private void initialById(View view)
    {
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRefchat = FirebaseDatabase.getInstance().getReference().child("Messages");

        //recycle
        recyclerChatList = view.findViewById(R.id.recycle_lis_chat_user);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            //
            loadHistoryChat();
        }
    }

    private void loadHistoryChat()
    {
        String current = mAuth.getUid();

        mRefchat.child(current).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String uid = data.getRef().getKey();
                    mRefUser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
                    mRefUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserInfo info = dataSnapshot.getValue(UserInfo.class);
                            Log.i("chat list",info.Name);

                            if (info != null)
                            {
                                infoList.add(info);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        adapter = new AllUserAdapter(getView().getContext().getApplicationContext(),infoList,"chat");

        //
        recyclerChatList.setHasFixedSize(true);
        recyclerChatList.setLayoutManager(layoutManager);
        recyclerChatList.setAdapter(adapter);
    }
}
