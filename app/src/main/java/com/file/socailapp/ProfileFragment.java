package com.file.socailapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

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
    private UserPostAdater adater;

    //
    private List<PostModel> models = new ArrayList<>();

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initial
        initialById(view);

        //setting card
        settingCardBackground();

        //edit button click
        editUserOnClick();

        //image
        browseImage();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {
            //detail
            getUserDetail();

            //
            loadPost();
        }
    }

    //initial
    private void initialById(View view)
    {
        txtName = view.findViewById(R.id.txt_user_name);
        txtNickName = view.findViewById(R.id.txt_user_nick_name);

        //image
        imageUser = view.findViewById(R.id.image_user);

        //card
        cardbg = view.findViewById(R.id.card_user_detail);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        //image button
        btnEditUser = view.findViewById(R.id.btn_edit_user_detail);
        btnBrowseImage = view.findViewById(R.id.btn_browse_image);

        //recycle view
        recyclerPost = view.findViewById(R.id.recycle_post_list_profile);
    }

    //setting card bg
    private void settingCardBackground()
    {
        cardbg.setBackgroundResource(R.drawable.bg_detail_user);
    }

    /*
    load user post for current user
     */
    private void loadPost()
    {
        String uid = mAuth.getUid();
        mRefPost = FirebaseDatabase.getInstance().getReference().child("Post");
        mRefPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                  if (data.child("uid").getValue().toString().equals(uid))
                  {
                      //current user post
                      PostModel model = data.getValue(PostModel.class);
                     // Log
                      if (model != null)
                      {
                          models.add(model);
                          adater.notifyDataSetChanged();
                      }
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
        adater = new UserPostAdater(getContext().getApplicationContext(),models);
        manager = new LinearLayoutManager(getContext().getApplicationContext());

        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(manager);
        recyclerPost.setAdapter(adater);
    }

    private void getUserDetail()
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        Log.i("uid",mAuth.getUid().toString());
        mRef.child(mAuth.getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    /*
    edit user detail
     - name
     - nick name
      * click
     */
    private void editUserOnClick()
    {
       btnEditUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               EditDialog dialog = new EditDialog();
               FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
               dialog.show(fragmentTransaction,"dialog edit");
           }
       });
    }

    /*
    browse image from g
     */
    private void browseImage()
    {
        btnBrowseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,0);
            }
        });
    }

    //get image result

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK && data != null)
        {
            uri = data.getData();
            saveImageUri(uri);
        }
    }

    private void saveImageUri(Uri image)
    {
        StorageReference sRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");

        String key = mRef.push().getKey();
        StorageReference path = sRef.child(key+""+sRef.getPath()+".jpg");

        path.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful())
                {
                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful())
                            {
                                Log.i("image uri",task.getResult().toString());

                                mRef.child(mAuth.getUid().toString()).child("Image").setValue(task.getResult().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful())
                                           {
                                               Log.i("save image uri","successful..");
                                           }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }
}
