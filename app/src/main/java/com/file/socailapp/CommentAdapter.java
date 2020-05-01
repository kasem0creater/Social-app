package com.file.socailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<ViewHolderComment>
{
    private List<CommentModel> comment = new ArrayList<>();
    private String postUid;
    private Context context;

    //firebase
    private DatabaseReference mRef;

    //constructor
    public CommentAdapter(Context context , List<CommentModel> commentModels ,  String postUid)
    {
        this.context = context;
        this.comment = commentModels;
        this.postUid = postUid;
    }

    @NonNull
    @Override
    public ViewHolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment , parent,false);

        ViewHolderComment viewHolderComment = new ViewHolderComment(view);
        return viewHolderComment;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComment holder, int position) {

        String uid = comment.get(position).getUid();
        String name = comment.get(position).getName();
        String body = comment.get(position).getMessage();
        String image = comment.get(position).getImage();
        String comId = comment.get(position).getCommentId();
        String date = comment.get(position).getDate();
        String time = comment.get(position).getTime();
        String postId = comment.get(position).getPostId();


        //load image profile for user comment this post
        loadProfileUser(uid,holder);

        ///check user comment with image or text
        if (image.isEmpty())
        {
            //todo comment with text
           //holder.imageComment.setVisibility(View.VISIBLE);
            holder.imageComment.setVisibility(View.GONE);
            //setting view
            holder.txtComment.setText(name+" \n"+body);
            holder.txtTime.setText(date+" : "+time);

        }
        else
        {
            //todo comment with image
            holder.imageComment.setVisibility(View.VISIBLE);
            //setting view
            holder.txtComment.setText(name+" \n"+body);
            holder.txtTime.setText(date+" : "+time);

            Picasso.get().load(image).into(holder.imageComment);
        }

        //btn setting comment -> txt Comment click
        holder.txtComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                settingPopup(holder,uid,postId,comId,image);
                return false;
            }
        });
    }

    private void settingPopup(ViewHolderComment h,String uid, String postId ,String cmId,String image)
    {
        PopupMenu menu = new PopupMenu(context.getApplicationContext(),h.txtComment);
        menu.getMenuInflater().inflate(R.menu.popup_menu,menu.getMenu());

        //
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int key = menuItem.getItemId();
                switch (key)
                {
                    case R.id.popup_delete_post:
                        removeComment(uid,postId,cmId,image);
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    private void removeComment(String uid, String postId , String cmId,String image)
    {
        if (image.isEmpty())
        {
            DatabaseReference cm = FirebaseDatabase.getInstance().getReference().child("Post");
            cm.child(postUid).child(postId).child("comments").child(cmId).removeValue();
        }
        else
        {
            //this comment there is image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("Comment").child("Images").child(cmId);
            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        DatabaseReference cm = FirebaseDatabase.getInstance().getReference().child("Post");
                        cm.child(postUid).child(postId).child("comments").child(cmId).removeValue();
                    }
                }
            });
        }
    }

    private void loadProfileUser(String uid,ViewHolderComment holder)
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("Image").getValue().toString();

                if (!image.isEmpty())
                {
                    Picasso.get().load(image).error(R.drawable.icon_profile).into(holder.imageprofile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return comment.size();
    }
}
