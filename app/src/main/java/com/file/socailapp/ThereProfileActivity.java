package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {

    //text view
    private TextView txtName , txtNickName;

    // image
    private ImageView imageUser;

    //card
    private CardView cardbg;

    //firebase
    private DatabaseReference mRef,mRefPost;
    private FirebaseAuth mAuth;

    //image button
    private ImageButton btnEditUser;
    private Button btnBrowseImage;

    //uri
    private Uri uri;

    //recycle view
    private RecyclerView recyclerPost;
    private RecyclerView.LayoutManager manager;
    private PostAdater adater;

    //
    private List<PostModel> models = new ArrayList<>();

    //String other user
    private String postId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

         postId = getIntent().getStringExtra("postId");
         Log.i("post Id", postId);
        //initial
        initialById();

    }

    private void initialById()
    {
        txtName = findViewById(R.id.txt_user_name);
        txtNickName = findViewById(R.id.txt_user_nick_name);

        //image
        imageUser = findViewById(R.id.image_user);

        //card
        cardbg = findViewById(R.id.card_user_detail);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        //image button
        btnEditUser = findViewById(R.id.btn_edit_user_detail);
        btnBrowseImage = findViewById(R.id.btn_browse_image);

        //recycle view
        recyclerPost = findViewById(R.id.recycle_post_list_profile);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //
        loadPost();
    }

    /*
   load user post for current id (friends)
    */
    private void loadPost()
    {
        models.clear();

        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefPost.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.i("load post profile",body);
                PostModel postModel =  dataSnapshot.getValue(PostModel.class);

                if (postModel != null)
                {
                    models.add(postModel);
                    adater.notifyDataSetChanged();

                    //
                    //detail
                    getUserDetail(postModel.getUid().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
        settingRecycleView();
    }


    private void getUserDetail(String s)
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    try
                    {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String nickName = dataSnapshot.child("NickName").getValue().toString();
                        String image = "";
                        image = dataSnapshot.child("Image").getValue().toString();

                        txtName.setText(name);
                        txtNickName.setText(nickName);

                        if(!image.isEmpty())
                        {
                            //image
                            Picasso.get().load(image).error(R.drawable.bg_profile).into(imageUser);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.w("Error",e.getMessage().toString());
                    }
                }
                Log.i("get detail","Successful..");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Error get user detail",""+databaseError.getMessage().toString());
            }
        });
    }

    private void settingRecycleView()
    {
        adater = new PostAdater(getApplicationContext(),models);
        manager = new LinearLayoutManager(getApplicationContext());

        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(manager);
        recyclerPost.setAdapter(adater);
    }

}
