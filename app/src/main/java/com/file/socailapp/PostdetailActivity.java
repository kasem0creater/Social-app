package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostdetailActivity extends AppCompatActivity {

    //view component
     //image
    private CircularImageView imageProfile;
     //text view
    private TextView txtName , txtTime,txtContent , txtLike,txtTotalCm;
     //image button setting
    private ImageButton btnSettingPost;
    private ImageView imageContent;
     //button like and shared
    private Button btnLike , btnShared;


    //comment view and action
     //image profile user of comment
    private CircularImageView imageprofileCm;
     //edit
    private EditText txtComment;
     //button
    private ImageButton btnComment,btnOpenImage;

    //String
    private static String postId;
    private static String uidPost;
    private static String userName;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRefPost , mRefLike,mRefUser ,mRefComment;
    private StorageReference sRef;


    //String
    private int totalLike;

    //image uri
    private Uri imageUri;
    private String imageURL;

    //recycle view
    private RecyclerView recyclerComment;
    private List<CommentModel> models = new ArrayList<>();
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        initialById();

        //get data from intent
        getStringExtra();

        //check text empty txt comment is empty
        //hide image profile comment
        checkTextEmpty();

        //add comment
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });


        //btn like click
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLike();
            }
        });


        //btn open image
        btnOpenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        //btn shared click
        btnShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String body = txtContent.getText().toString();
                try {
                    shared(body);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        //btn setting click
        btnSettingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingPost(postId , uidPost);
            }
        });
    }

    private void settingPost(String postId, String uidPost)
    {
        PopupMenu menu = new PopupMenu(getApplicationContext() ,btnSettingPost);
        MenuInflater layoutInflater = menu.getMenuInflater();
        layoutInflater.inflate(R.menu.popup_menu , menu.getMenu());

        //
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int key = menuItem.getItemId();
                switch (key)
                {
                    case R.id.popup_delete_post:
                        removePost(postId , uidPost);
                        break;
                    case R.id.popup_edit:
                        editPost(postId , uidPost);
                        break;
                }
                return false;
            }
        });

        //
        menu.show();
    }

    private void editPost(String postId, String uidPost)
    {
        //edit post
        String current = mAuth.getUid();

        if (uidPost.equals(current))
        {
            //
            Intent intent = new Intent(getApplicationContext() , PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postId",postId);
            intent.putExtra("uid",uidPost);

            startActivity(intent);
        }
        else
        {
            // another user
        }
    }

    private void removePost(String postId, String uidPost)
    {
        sRef = FirebaseStorage.getInstance().getReference().child("UserPostImage")
                .child("image").child(postId);

        DatabaseReference mRemove = FirebaseDatabase.getInstance().getReference()
                .child("Post");

        sRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mRemove.child(postId).removeValue();
                }
            }
        });
    }

    //
    private void shared(String body) throws IOException {
        //get image from image view
        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageContent.getDrawable();
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

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM , uri);
        intent.putExtra(Intent.EXTRA_TEXT,body);
        intent.putExtra(Intent.EXTRA_SUBJECT,"ReBuild");
        intent.setType("image/ping");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent ,"Shared"));
    }

    private Uri saveImageToShared(Bitmap bitmap) throws IOException {
        //name images == images name in xml paths
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri = null;

        imageFolder.mkdirs();//create folder
        File file = new File(imageFolder,"shared_image.ping");

        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
        stream.flush();
        stream.close();

        uri = FileProvider.getUriForFile(getApplicationContext(),"com.file.socailapp.fileprovider",file);

        return  uri;
    }

    private void sharedText(String body)
    {
        String sharedBody = body;

        //setting shared intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT , "ReBuild");
        intent.putExtra(Intent.EXTRA_TEXT,sharedBody);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent,"Shared"));

    }

    //
    private void initialById()
    {
        //image
        imageProfile = findViewById(R.id.image_circle_profile_post_list);
        imageprofileCm = findViewById(R.id.image_profile_user_comment);
        imageContent = findViewById(R.id.image_post_content_list);

        //text
        txtName = findViewById(R.id.txt_user_post_list);
        txtLike = findViewById(R.id.txt_show_like_post_list);
        txtTime = findViewById(R.id.txt_time_post_list);
        txtContent = findViewById(R.id.txt_content_post_list);
        txtTotalCm = findViewById(R.id.txt_show_comment_post_list);

        //
        txtComment = findViewById(R.id.txt_comment);

        //button
        btnLike = findViewById(R.id.btn_like_post_list);
        btnShared = findViewById(R.id.btn_shared_post_list);
        btnSettingPost = findViewById(R.id.btn_post_setting_list);
        btnComment = findViewById(R.id.btn_comment);
        btnOpenImage = findViewById(R.id.btn_comment_image);


        //firebase
        mAuth = FirebaseAuth.getInstance();

        //recycle view
        recyclerComment = findViewById(R.id.recycle_comment_list);
    }

    private void getStringExtra()
    {
        postId = getIntent().getStringExtra("postId");
        uidPost = getIntent().getStringExtra("uidPost");
    }

    private void checkTextEmpty()
    {
        txtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty())
                {
                    imageprofileCm.setVisibility(View.GONE);
                    btnOpenImage.setVisibility(View.GONE);
                }
                else
                {
                    imageprofileCm.setVisibility(View.VISIBLE);
                    btnOpenImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            loadPostDetail();

            //load like
            loadLikes();

            //load comment all user
            loadComment();

            //laod image profile user as comment
            loadProfileUserCm();

        }
    }

    private void loadProfileUserCm()
    {
        String uid = mAuth.getUid();

        mRefUser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRefUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image =dataSnapshot.child("Image").getValue().toString();
                //userName = dataSnapshot.child("Name").getValue().toString();

                if(!image.isEmpty())
                {
                    Picasso.get().load(image).into(imageprofileCm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadComment()
    {
        DatabaseReference loadCm = FirebaseDatabase.getInstance().getReference().child("Post");
        loadCm.child(postId.toString()).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    CommentModel model = data.getValue(CommentModel.class);

                    if (model != null)
                    {
                       // Log.i("load comment",""+model.getMessage()+model.getImage());
                        models.add(model);
                        adapter.notifyDataSetChanged();
                    }
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
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        adapter = new CommentAdapter(getApplicationContext() , models,uidPost);

        //
        recyclerComment.setHasFixedSize(true);
        recyclerComment.setLayoutManager(manager);
        recyclerComment.setAdapter(adapter);
    }

    private void loadLikes()
    {
        String current = mAuth.getUid();

        mRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mRefLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(current))
                {
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_liked,0,0,0);
                    btnLike.setText("Liked");
                }
                else
                {
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like,0,0,0);
                    btnLike.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean mProcessLike = false;
    private void userLike()
    {
        String current = mAuth.getUid();

        mProcessLike = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Likes");
        DatabaseReference refUpdateLike = FirebaseDatabase.getInstance().getReference("Post");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike)
                {
                   if (dataSnapshot.child(postId).hasChild(current))
                   {
                       //int totalcm = Integer.parseInt(dataSnapshot.child(uidPost).child(postId).child("like").toString());
                          refUpdateLike.child(postId).child("like").setValue(""+(totalLike-1));
                          ref.child(postId).child(current).removeValue();
                          mProcessLike = false;
                   }
                   else
                   {
                       //int totalcm = Integer.parseInt(dataSnapshot.child(uidPost).child(postId).child("like").toString());
                       refUpdateLike.child(postId).child("like").setValue(""+(totalLike+1));
                       ref.child(postId).child(current).setValue("Liked");
                       mProcessLike = false;
                   }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostDetail()
    {
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefPost.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                PostModel model = dataSnapshot.getValue(PostModel.class);


                loadProfileUserPost(model.getUid());
                //
                if(model.getImage().isEmpty())
                {
                    //user post with text
                    imageContent.setVisibility(View.GONE);

                    txtName.setText(model.getName());
                    txtTime.setText(model.getDate()+": "+model.getTime());
                    txtContent.setText(model.getBody());
                    txtLike.setText(model.getLike()+" Likes");
                    txtTotalCm.setText(model.getTotalComment()+" comments");

                }
                else
                {
                    //user post with image and text
                    imageContent.setVisibility(View.VISIBLE);

                    txtName.setText(model.getName());
                    txtTime.setText(model.getDate()+": "+model.getTime());
                    txtContent.setText(model.getBody());
                    txtLike.setText(model.getLike()+" Likes");
                    txtTotalCm.setText(model.getTotalComment()+" comments");

                    //set image
                    Picasso.get().load(model.getImage()).error(R.drawable.icon_profile).into(imageContent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadProfileUserPost(String uid)
    {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRefUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image =dataSnapshot.child("Image").getValue().toString();
                userName = dataSnapshot.child("Name").getValue().toString();

                if(!image.isEmpty())
                {
                    Picasso.get().load(image).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //
    private void addComment()
    {
        String comment = txtComment.getText().toString().trim();

            String current = mAuth.getUid();

            String key = mRefPost.push().getKey();

            if(imageUri == null)
            {
                if (!TextUtils.isEmpty(comment))
                {
                    // text
                    updatePost(comment,key,current,"");
                }
            }
            else
            {
                //image

                //
                String path = "Images/"+key;
                sRef = FirebaseStorage.getInstance().getReference().child("Comment").child(path);
                sRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if(task.isSuccessful())
                                    {
                                        //update image url ot post
                                        imageURL = task.getResult().toString();
                                        updatePost(comment , key ,current , imageURL);
                                    }
                                }
                            });
                        }
                    }
                });
            }

    }

    private void updatePost(String comment, String key, String current , String image)
    {
        mRefComment = FirebaseDatabase.getInstance().getReference().child("Post").child(postId).child("comments");

        Calendar calendar =  Calendar.getInstance();

        SimpleDateFormat stime = new SimpleDateFormat("HH:mm:ss a");
        SimpleDateFormat sdate = new SimpleDateFormat("dd-MM-yyyy");

        String time = stime.format(calendar.getTime());
        String date = sdate.format(calendar.getTime());

        Map<String,Object> map = new HashMap<>();
        map.put("commentId",key);
        map.put("postId",postId);
        map.put("name",userName);
        map.put("date",date);
        map.put("time",time);
        map.put("image",image+"");
        map.put("message",comment);
        map.put("uid",current);

        mRefComment.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    txtComment.setText("");
                    Log.i("add comment","successful..");
                    updateCommentCount();
                }
            }
        });

    }


    private boolean mProcressComment = false;
    private void updateCommentCount()
    {
        mProcressComment = true;
        DatabaseReference mCommentCount = FirebaseDatabase.getInstance().getReference("Post");

        mCommentCount.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcressComment)
                {
                    String cm = dataSnapshot.child("totalComment").getValue().toString();
                       int totalComment = Integer.parseInt(cm);
                   // Log.i("get Comment","j "+k+totalComment);
                    mCommentCount.child(postId).child("totalComment").setValue(""+(totalComment+1));
                    mProcressComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openImage()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
        }
    }
}
