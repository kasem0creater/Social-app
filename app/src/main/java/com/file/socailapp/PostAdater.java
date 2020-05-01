package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostAdater extends RecyclerView.Adapter<ViewHolderPost>
{
    private Context context;
    private List<PostModel> post = new ArrayList<>();
    private String imProfile;

    //
    private boolean processLike=false;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mRefPost ,mRefLike;
    private StorageReference sRef;



    public PostAdater(Context applicationContext, List<PostModel> models )
    {
        this.context = applicationContext;
        this.post = models;
    }


    @NonNull
    @Override
    public ViewHolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_text , parent , false);

        //initial
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");


        ViewHolderPost  holderPost = new ViewHolderPost(view);
        return holderPost;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPost holder, int position)
    {
        //setting item
        String uid = post.get(position).getUid();
        String name = post.get(position).getName();
        String body = post.get(position).getBody();
        String image = post.get(position).getImage()+"";
        String date = post.get(position).getDate();
        String time = post.get(position).getTime();
        String like = post.get(position).getLike();
        String postId = post.get(position).getPostId();
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

        //set like each like from post
        settingLiked( holder ,  postId);


        //check image post
        if (image.isEmpty())
        {
            //todo user post with text
            holder.txtName.setText(name);
            holder.txtTime.setText(""+date+" :"+time+" min");
            holder.txtLike.setText(like+" Likes");
            holder.txtComment.setText(totalcomment+" Comments");
            holder.txtContentPost.setText(body);
        }
        else
        {
            //todo user post with image
            holder.imagePostContent.setVisibility(View.VISIBLE);

            holder.txtName.setText(name);
            holder.txtTime.setText(""+date+" :"+time+" min");
            holder.txtLike.setText(like+" Likes");
            holder.txtComment.setText(totalcomment+" Comments");
            holder.txtContentPost.setText(body);

            Picasso.get().load(image).error(R.drawable.icon_profile).into(holder.imagePostContent);
        }

        //profile click
        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendToThereProfile(postId);
            }
        });


        //name click
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToThereProfile(postId);
            }
        });

        //user like post
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int totalLike = Integer.parseInt(post.get(position).getLike().toString());
                processLike = true;

                // user like save to uid user of like
                mRefLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(processLike)
                        {
                            String Myuid = mAuth.getUid();

                            //check if there is post id and uid = current id
                            // remove like
                            if(dataSnapshot.child(postId).hasChild(Myuid))
                            {
                                // case user like
                                    mRefPost.child(postId).child("like").setValue(""+(totalLike-1));
                                    mRefLike.child(postId).child(Myuid).removeValue();
                                    processLike = false;

                            }
                            else
                            {
                                //case user not like
                                mRefPost.child(postId).child("like").setValue(""+(totalLike+1));
                                mRefLike.child(postId).child(Myuid).setValue("liked");
                                processLike = false;
                            }
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

                openCommentActivity(postId ,uid);
            }
        });
        holder.txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentActivity(postId ,uid);
            }
        });


        //btn setting post click
        holder.btnSettingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingPopup(holder,postId,uid, image);
            }
        });

        //btn shared
        holder.btnShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    shared(holder,image , body);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        //txt like click open to like by who
        holder.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLikeByPage(postId , uid);
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

    private void shared(ViewHolderPost h ,String image, String body) throws IOException {
        //get image from image view
        BitmapDrawable bitmapDrawable = (BitmapDrawable)h.imagePostContent.getDrawable();
        //check null image
        if (bitmapDrawable == null)
        {
            sharedText(body);
        }
        else
        {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            sharedImageAndText(bitmap,body);
        }
    }

    private void sharedImageAndText(Bitmap bitmap, String body) throws IOException {
        Uri uri = saveImageToShared(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM , uri);
        intent.putExtra(Intent.EXTRA_TEXT,body);
        intent.putExtra(Intent.EXTRA_SUBJECT,"ReBuild");
        intent.setType("image/ping");

        Intent sharedintent = Intent.createChooser(intent,"Shared");
        sharedintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sharedintent);
    }

    private Uri saveImageToShared(Bitmap bitmap) throws IOException {
        //name images == images name in xml paths
        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;

        imageFolder.mkdirs();//create folder
        File file = new File(imageFolder,"shared_image.ping");

        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
        stream.flush();
        stream.close();

        uri = FileProvider.getUriForFile(context,"com.file.socailapp.fileprovider",file);

        return  uri;
    }

    private void sharedText(String body)
    {
        String sharedBody = body;

        //setting shared intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT , "ReBuild");
        intent.putExtra(Intent.EXTRA_TEXT,sharedBody);

        Intent sharedintent = Intent.createChooser(intent,"Shared");
        sharedintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sharedintent);

    }

    private void settingPopup(ViewHolderPost h , String postId,String uid,String image)
    {
        PopupMenu menu = new PopupMenu(context.getApplicationContext() , h.btnSettingPost);
        menu.getMenuInflater().inflate(R.menu.popup_menu,menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int key = menuItem.getItemId();
                switch (key)
                {
                    case R.id.popup_delete_post:
                        removePost(postId,uid , image);
                        break;
                    case R.id.popup_edit:
                        editPost(postId,uid);
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    private void editPost(String postId, String uid)
    {
        //edit post
        String current = mAuth.getUid();

       if (uid.equals(current))
       {
           //
           Intent intent = new Intent(context.getApplicationContext() , PostActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.putExtra("postId",postId);
           intent.putExtra("uid",uid);

           context.startActivity(intent);
       }
       else
       {
           // another user
       }
    }

    private void removePost(String postId,String uid , String image)
    {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
     if (image.isEmpty())
     {
         mRefPost.child(postId).removeValue();
         likeRef.child(postId).removeValue();
     }
     else
     {
         sRef = FirebaseStorage.getInstance().getReference().child("UserPostImage")
                 .child("image").child(postId);
         sRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful())
                 {
                     mRefPost.child(postId).removeValue();
                     likeRef.child(postId).removeValue();
                 }
             }
         });
     }
    }

    private void openCommentActivity(String postId , String uid)
    {
        Intent intent = new Intent(context.getApplicationContext() , PostdetailActivity.class);
        intent.putExtra("postId",postId);
        intent.putExtra("uidPost",uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void settingLiked(ViewHolderPost holderPost , String postId)
    {
        String uid = mAuth.getUid();

        mRefLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(uid))
                {
                    //user has like this post

                    holderPost.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_liked,0,0,0);
                    holderPost.btnLike.setText("Liked");

                }
                else
                {
                    //user  has not like this post

                    holderPost.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like,0,0,0);
                    holderPost.btnLike.setText("Like");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendToThereProfile(String uid)
    {
        Intent intent = new Intent(context.getApplicationContext() ,ThereProfileActivity.class);
        intent.putExtra("postId",uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return post.size();
    }
}
