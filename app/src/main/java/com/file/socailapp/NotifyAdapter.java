package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
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

public class NotifyAdapter extends RecyclerView.Adapter<ViewHolderNotify>
{

    private List<NotifyModel> models = new ArrayList<>();
    private Context context;

    //firebase
    private DatabaseReference mRefUser;
    private FirebaseAuth mAuth;

    public NotifyAdapter(Context context , List<NotifyModel> models)
    {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolderNotify onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_layout , parent , false);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRefUser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");

        ViewHolderNotify notify = new ViewHolderNotify(view);
        return notify;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotify holder, int position) {

        String uid = models.get(position).getUid();
        String postId = models.get(position).getPostId();
        String message = models.get(position).getMessage();
        String date = models.get(position).getDate();
        String time = models.get(position).getTime();

        String  dateTime = date+" :"+time;

        //
        loadImageProfile(holder , uid);

        //setting to view

        holder.txtNotify.setText(""+message+" \n"+dateTime);


        //click open notify
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailNotify(uid,postId);
            }
        });
    }

    private void openDetailNotify(String uid, String postId)
    {
        Intent intent = new Intent(context.getApplicationContext() , PostdetailActivity.class);
        intent.putExtra("postId",postId);
        intent.putExtra("uidPost",uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void loadImageProfile(ViewHolderNotify holder, String uid)
    {
        mRefUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("Image").getValue().toString();
                String name = dataSnapshot.child("Name").getValue().toString();


                holder.txtName.setText(name);
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
