package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WhoLikeAdapter extends RecyclerView.Adapter<ViewHolderWhoLike>
{
    //
    private List<LikeModel> models = new ArrayList<>();
    private String type;
    private Context context;

    //firebase
    private DatabaseReference mRef;


    public WhoLikeAdapter(Context context,List<LikeModel> likeModels , String type)
    {
        this.context = context;
        this.models = likeModels;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolderWhoLike onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_all_user,parent,false);

        //initial
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");

        ViewHolderWhoLike like = new ViewHolderWhoLike(view);
        return like;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWhoLike holder, int position) {

        String uid = models.get(position).getUid();

        //check type adapter
        //is chat hi menu setting
        //is list user show menu setting


        if (type.equals("chat"))
        {
            holder.btn_User.setVisibility(View.GONE);
        }
        else if (type.equals("Like"))
        {
            holder.btn_User.setVisibility(View.GONE);
        }
        else
        {
            holder.btn_User.setVisibility(View.VISIBLE);
        }

        //get user info from uid
        getUerInfoLike(holder,uid);

        //profile click
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendToThereProfile(uid);
            }
        });


        //name click
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToThereProfile(uid);
            }
        });

    }


    private void sendToThereProfile(String uid)
    {
        Intent intent = new Intent(context.getApplicationContext() ,ThereProfileActivity.class);
        intent.putExtra("uid",uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void getUerInfoLike(ViewHolderWhoLike holder, String uid)
    {
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();
                String nick = dataSnapshot.child("NickName").getValue().toString();

                //set text as view
                holder.txtName.setText(name);
                holder.txtNickName.setText(nick);

                if (status.equals("online"))
                {
                    holder.userStatus.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.userStatus.setVisibility(View.GONE);
                }

                //check image empty
                if (!image.isEmpty())
                {
                    Picasso.get().load(image).error(R.drawable.icon_profile).into(holder.imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return models.size();
    }
}
