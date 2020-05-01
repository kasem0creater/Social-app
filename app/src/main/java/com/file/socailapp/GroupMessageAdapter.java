package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageHolder>
{

    private List<GroupMessageModel> models = new ArrayList<>();

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mRemove;

    //
    private Context context;

    public GroupMessageAdapter(Context context ,List<GroupMessageModel> messageEntities)
    {
      this.context = context;
      this.models = messageEntities;

    }

    @NonNull
    @Override
    public GroupMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout , parent , false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRemove =FirebaseDatabase.getInstance().getReference().child("Groups");

        GroupMessageHolder holder = new GroupMessageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageHolder holder, int position) {

        String message = models.get(position).getMessage();
        String messageId = models.get(position).getMessageId();
        String currentUid = mAuth.getUid();
        String uid = models.get(position).getSender();
        String type = models.get(position).getType();
        String groupId = models.get(position).getGroupId();


        //load image profile sender user
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageProfile = dataSnapshot.child("Image").getValue().toString();

                //if
                if(!imageProfile.isEmpty())
                {
                    Picasso.get().load(imageProfile).error(R.drawable.ic_account_circle_black_24dp).into(holder.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //hide view component

        //text
        holder.txtSender.setVisibility(View.GONE);
        holder.txtFriend.setVisibility(View.GONE);

        //profile
        holder.profile.setVisibility(View.GONE);

        //image
        holder.imageSender.setVisibility(View.GONE);
        holder.imageFriend.setVisibility(View.GONE);

        //check image type
        if (type.equals("text"))
        {
            //user send with text
            if (uid.equals(currentUid))
            {
                //check user send is current user
                //if yes give show text in sender text
                holder.txtSender.setVisibility(View.VISIBLE);

                holder.txtSender.setText(message);
            }
            else
            {
                //other user send
                holder.txtFriend.setVisibility(View.VISIBLE);
                holder.profile.setVisibility(View.VISIBLE);

                //set message
                holder.txtFriend.setText(message);
            }
        }
        else  if (type.equals("image"))
        {
            //user send with image
        }
        //


        //txt sender click remove message
        holder.txtSender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                removeMessage(groupId,messageId);
                return false;
            }
        });


    }

    private void removeMessage(String groupId , String messageId)
    {
        mRemove.child(groupId).child("message").child(messageId).removeValue();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
