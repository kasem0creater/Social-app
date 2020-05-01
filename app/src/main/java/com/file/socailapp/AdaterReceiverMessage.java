package com.file.socailapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdaterReceiverMessage extends RecyclerView.Adapter<ViewHolderReceiver>
{
    private List<MessageEntity> listMessage = new ArrayList<>();
    private List<String> listKey = new ArrayList<>();

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mRemove;


    public AdaterReceiverMessage(List<MessageEntity> messageEntities , List<String> key)
    {
        this.listMessage = messageEntities;
        this.listKey = key;
    }

    @NonNull
    @Override
    public ViewHolderReceiver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent , false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRemove = FirebaseDatabase.getInstance().getReference().child("Messages");

        ViewHolderReceiver viewHolderReceiver = new ViewHolderReceiver(view);
        return viewHolderReceiver;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderReceiver holder, int position) {

        //item
        String from = listMessage.get(position).getFrom();
        String message = listMessage.get(position).getMessage();
        String type = listMessage.get(position).getType();
        String uid = mAuth.getUid();
        String to = listMessage.get(position).getTo();
        Log.i("uid",uid);


        //get image profile friend

        mRef.child(from).addValueEventListener(new ValueEventListener() {
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

        //

       if(type.equals("text"))
       {
           if(from.equals(uid))
           {
               //todo sender view
               //check type message
                   holder.txtSender.setVisibility(View.VISIBLE);
                   //set message
                   holder.txtSender.setText(message);
                   Log.i("get message",""+message);


                   //remove message for user
               holder.txtSender.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View view) {


                       mRemove.child(from).child(to).child(listKey.get(position).toString()).removeValue()
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           mRemove.child(to).child(from).child(listKey.get(position).toString())
                                                   .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful())
                                                   {
                                                       listMessage.remove(position);
                                                       listKey.remove(position);
                                                       notifyDataSetChanged();
                                                       Log.i("remove message","successful..");
                                                   }
                                                   else
                                                   {
                                                       Log.w("remove message friend","error");
                                                   }
                                               }
                                           });
                                       }
                                       //
                                       else
                                       {
                                           Log.w("remove message sender","error");
                                       }
                                   }
                               });
                       return false;
                   }
               });

           }
           else
           {
               //todo friend view
               //check type message
               holder.txtFriend.setVisibility(View.VISIBLE);
               holder.profile.setVisibility(View.VISIBLE);

                   //set message
               holder.txtFriend.setText(message);

           }

           //image


       }

    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }
}
