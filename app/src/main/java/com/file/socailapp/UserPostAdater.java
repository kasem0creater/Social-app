package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserPostAdater extends RecyclerView.Adapter<ViewHolderUserPost> {

    private Context context;
    private List<PostModel> post = new ArrayList<>();
    private String imProfile;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,mRefPost,mRefLike;
    private StorageReference sRef;

    //
    private boolean processLike=false;

    public UserPostAdater(Context applicationContext, List<PostModel> models)
    {
        this.context = applicationContext;
        this.post = models;
    }

    @NonNull
    @Override
    public ViewHolderUserPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_text , parent , false);

        //initial
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");


        mRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");

        ViewHolderUserPost  holderPost = new ViewHolderUserPost(view);
        return holderPost;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUserPost holder, int position) {

        //setting item
        String uid = post.get(position).getUid();
        String name = post.get(position).getName();
        String body = post.get(position).getBody();
        String image = post.get(position).getImage()+"";
        String date = post.get(position).getDate();
        String time = post.get(position).getTime();
        int totallike = Integer.parseInt(post.get(position).getLike());
        String keyPost = post.get(position).getPostId();
        String totalcomment = post.get(position).getTotalComment();

        //setting get profile from current user
        mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imProfile = dataSnapshot.child("Image").getValue().toString();

                if (!imProfile.isEmpty())
                {
                    Picasso.get().load(imProfile.trim()).error(R.drawable.icon_profile_).into(holder.imageprofile);
                    Log.i("load image",imProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //load like of user this post
        loadLike(holder , keyPost);


        //check image post
        if (image.isEmpty())
        {
            //todo user post with text
            holder.txtName.setText(name);
            holder.txtTime.setText(""+date+" :"+time+" min");
            holder.txtContentPost.setText(body);
            holder.txtLike.setText(totallike+" like");
            holder.txtComment.setText(totalcomment+" Comments");
        }
        else
        {
            //todo user post with image
            holder.imagePostContent.setVisibility(View.VISIBLE);

            holder.txtName.setText(name);
            holder.txtTime.setText(""+date+" :"+time+" min");
            holder.txtContentPost.setText(body);
            holder.txtLike.setText(totallike+" like");
            holder.txtComment.setText(totalcomment+" Comments");

            Picasso.get().load(image).error(R.drawable.icon_profile).into(holder.imagePostContent);
        }


        //user press like button
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context.getApplicationContext() ,"click",Toast.LENGTH_LONG).show();
                //check user like
                processLike = true;

                mRefLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(processLike)
                        {
                            String currentUser = mAuth.getUid();
                            //check data user like that path like
                            if (dataSnapshot.child(keyPost).hasChild(uid))
                            {
                                // there is data user like of this post
                                mRefPost.child(keyPost).child("like").setValue(""+(totallike-1));
                                mRefLike.child(keyPost).child(currentUser).removeValue();
                                processLike = false;

                            }
                            else
                            {
                                //there is data not user like not this post
                                mRefPost.child(keyPost).child("like").setValue(""+(totallike+1));
                                mRefLike.child(keyPost).child(currentUser).setValue("liked");
                                processLike = false;
                            }
                        }
                        else
                        {
                            Log.i("Like","not Click");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //btn comment click
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              opentCommentActivity(keyPost ,uid);
            }
        });
        holder.txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opentCommentActivity(keyPost ,uid);
            }
        });

        //btn setting click
        holder.btnSettingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingPopupMenu(holder,keyPost,uid,image);
            }
        });

        //txt like click open to like by who
        holder.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLikeByPage(keyPost , uid);
            }
        });
    }

    private void settingPopupMenu(ViewHolderUserPost holder, String keyPost, String uid , String image)
    {
        PopupMenu item = new PopupMenu(context.getApplicationContext(),holder.btnSettingPost);
        item.getMenuInflater().inflate(R.menu.popup_menu , item.getMenu());

        item.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int key = menuItem.getItemId();
                switch (key)
                {
                    case R.id.popup_delete_post:
                        removePost(keyPost,uid , image);
                        break;
                    case R.id.popup_edit:
                        editPost(keyPost,uid);
                        break;
                }
                return false;
            }
        });

        //show
        item.show();
    }

    private void editPost(String key, String uid)
    {
        //edit post
        String current = mAuth.getUid();

        if (uid.equals(current))
        {
            //
            Intent intent = new Intent(context.getApplicationContext() , PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postId",key);
            intent.putExtra("uid",uid);

            context.startActivity(intent);
        }
        else
        {
            // another user
        }
    }

    private void removePost(String key, String uid , String image)
    {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        if (image.isEmpty())
        {
            mRefPost.child(key).removeValue();
        }
        else
        {
            sRef = FirebaseStorage.getInstance().getReference().child("UserPostImage")
                    .child("image").child(key);
            sRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        mRefPost.child(key).removeValue();
                        likeRef.child(key).removeValue();
                    }
                }
            });
        }
    }

    private void opentCommentActivity(String keyPost, String uid)
    {
        Intent intent = new Intent(context.getApplicationContext(),PostdetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("postId",keyPost);
        intent.putExtra("uidPost",uid);
        context.startActivity(intent);
    }


    private void loadLike(ViewHolderUserPost holder, String keyPost)
    {
        String current = mAuth.getUid();

        mRefLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(keyPost).hasChild(current))
                {
                    //user like of this post
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_liked ,0,0,0);
                    holder.btnLike.setText("Liked");
                    Log.i("Like","Like"+current);
                }
                else
                {
                    //user like not this post
                    holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like ,0,0,0);
                    holder.btnLike.setText("Like");
                    Log.i("Like","UnLike"+current);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void toLikeByPage(String postId, String uid)
    {
        Intent intent = new Intent(context.getApplicationContext() , LikeByActivity.class);

        intent.putExtra("uid",uid);
        intent.putExtra("posId",postId);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return post.size();
    }
}
