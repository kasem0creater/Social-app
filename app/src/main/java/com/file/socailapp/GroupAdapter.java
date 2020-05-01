package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder>
{
    private List<GroupModel> models = new ArrayList<>();
    private Context context;

    //firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;


    public GroupAdapter(Context context , List<GroupModel> models)
    {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_layout,parent , false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        GroupHolder holder = new GroupHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position)
    {
        String groupName = models.get(position).getGroupTitle();
        String time = models.get(position).getDateTime();
        String image = models.get(position).getGroupIcon();
        String groupId = models.get(position).getGroupId();


        Log.i("load group size",""+groupName);
        holder.txtGroupName.setText(groupName);
        holder.txtGroupMessage.setText("Hi message");
        holder.txtLastSeenTime.setText(time);

        //setting image
        if (!image.isEmpty())
        {
            Picasso.get().load(image).error(R.drawable.icon_profile).into(holder.imageGroup);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGroupChat(groupId);
            }
        });

        //btn group setting click
        // action ->
        //
        holder.btnGroupSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingPopup(holder,groupId);
            }
        });
    }

    private void settingPopup(GroupHolder holder, String groupId)
    {
        PopupMenu menu = new PopupMenu(context.getApplicationContext(), holder.btnGroupSetting);
        MenuInflater menuInflater = menu.getMenuInflater();
        menuInflater.inflate(R.menu.group_menu_setting , menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int key = menuItem.getItemId();

                switch (key)
                {
                    case R.id.menu_group_add_friends:
                        addFriendsToGroup(groupId);
                        break;
                }
                return false;
            }
        });

        //
        menu.show();

    }

    private void addFriendsToGroup(String groupId)
    {
        Intent intent = new Intent(context.getApplicationContext() , AddFriendToGroupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupId",groupId);
        context.startActivity(intent);

    }

    private void openGroupChat(String groupId)
    {
        Intent intent = new Intent(context.getApplicationContext() , GroupChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupId",groupId);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
