package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.DialogCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PostActivity extends AppCompatActivity {

    //image
    private CircularImageView imageProfile;
    private ImageButton btnOpenImage;
    private ImageView imageContent;

    //button
    private Button btnPost;


    //text
    private EditText txtMessage;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef , mRefPost,mRefEditPost,mRefNotify;
    private StorageReference sRef,sRefRemove;

    //uri image user
    private Uri selecteImageUri;
    private String imageURL;

    //string
    private static String userName;
    private static String postId;
    //uid of post
    private static String userId;
    private boolean mProcessPost = false;

    private ProgressDialog dialog;
    private boolean mProcessImage = false;
    private boolean mRrpcessNotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        //initialById();
        initialById();

        getStringExtra();

        //
        loadTextImageOtherApp();
        //
        checkTextEmpty();

        //image click open file
        clickImage();

        //user post click
        userPost();


    }

    private void loadTextImageOtherApp()
    {

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            if ("text/plain".equals(type))
            {
                //get text data
                handleSendText(intent);
            }
            else if ("image/*".equals(type))
            {
                //image data
                handleSendImage(intent);
            }
        }

    }

    private void handleSendImage(Intent intent)
    {
        Uri uri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null)
        {
            selecteImageUri = uri;
            mProcessImage = true;
            imageContent.setImageURI(uri);
        }
    }

    private void handleSendText(Intent intent)
    {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null)
        {
            txtMessage.setText(text);
        }
    }

    private void getStringExtra()
    {

        userId = getIntent().getStringExtra("uid");
        if (userId != null)
        {
            mProcessPost = true;
            postId = getIntent().getStringExtra("postId");
            loadEditPost();
        }
    }

    private void loadEditPost()
    {
        if (postId != null)
        {
            mRefEditPost = FirebaseDatabase.getInstance().getReference().child("Post");
            mRefEditPost.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String body = dataSnapshot.child("body").getValue().toString();
                     imageURL = dataSnapshot.child("image").getValue().toString()+"";
                    String date =dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String like = dataSnapshot.child("like").getValue().toString();
                    String status = dataSnapshot.child("status_post").getValue().toString();
                    String totalComment = dataSnapshot.child("totalComment").getValue().toString();


                    txtMessage.setText(body);
                    if (imageURL.isEmpty())
                    {
                        //
                        imageContent.setVisibility(View.GONE);
                    }
                    else
                    {
                        imageContent.setVisibility(View.VISIBLE);
                        Picasso.get().load(imageURL).into(imageContent);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void initialById()
    {
        //image
        imageProfile = findViewById(R.id.image_circle_profile_post);
        btnOpenImage = findViewById(R.id.btn_open_image);
        imageContent = findViewById(R.id.image_post_content);

        //button
        btnPost = findViewById(R.id.btn_post);

        //text
        txtMessage = findViewById(R.id.txt_post_content);


        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        sRef = FirebaseStorage.getInstance().getReference();

        //dialog
        dialog = new ProgressDialog(getApplicationContext());
    }


    @Override
    protected void onStart() {


        //load image profile
        loadImageprofile();

        super.onStart();
    }

    private void loadImageprofile()
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("Image").getValue().toString();
                userName  = dataSnapshot.child("Name").getValue().toString();

                //set hint text
                txtMessage.setHint("Hi .."+userName);

                if (!image.isEmpty())
                {
                    Picasso.get().load(image).error(R.drawable.bg_profile).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkTextEmpty()
    {
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty())
                {
                    txtMessage.setText("\n");
                }
            }
        });
    }

    private void userPost()
    {
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // upload image to file storage
                mRefPost = FirebaseDatabase.getInstance().getReference();

                String key = mRefPost.push().getKey();

                //
                if (!TextUtils.isEmpty(txtMessage.getText().toString()))
                {

                    //check user post image with text
                    if (selecteImageUri != null)
                    {
                        //post with image

                        //check edit post
                        if (mProcessImage)
                        {
                            //edit post and remove image
                            sRefRemove = FirebaseStorage.getInstance().getReference()
                                    .child("UserPostImage").child("image").child(postId);
                            sRefRemove.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        //
                                        String filePath = "UserPostImage/"+"image/"+postId+"";

                                        //dialog show
                                        // settingDialog("Post","please wait ...");

                                        //firebase path
                                        StorageReference path = sRef.child(filePath);
                                        path.putFile(selecteImageUri)
                                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                        if (task.isSuccessful())
                                                        {
                                                            //get image url
                                                            path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Uri> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        String uri = task.getResult().toString();
                                                                        mRrpcessNotify = false;
                                                                        //save post to db
                                                                        updatePost(txtMessage.getText().toString(),uri , postId);
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
                        else
                        {
                            //new post
                            String filePath = "UserPostImage/"+"image/"+key+"";

                            //dialog show
                            // settingDialog("Post","please wait ...");

                            //firebase path
                            StorageReference path = sRef.child(filePath);
                            path.putFile(selecteImageUri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if (task.isSuccessful())
                                            {
                                                //get image url
                                                path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            String uri = task.getResult().toString();
                                                            mRrpcessNotify = true;
                                                            //save post to db
                                                            updatePost(txtMessage.getText().toString(),uri , key);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                            //
                        }
                    }
                    else
                    {
                      //check user not change image
                        if (imageURL == null)
                        {
                            // user post with text and new post

                            //text post

                            //dialog show
                            // settingDialog("Post","please wait ...");

                            updatePost(txtMessage.getText().toString() , "",key);
                            mRrpcessNotify = true;
                        }
                        else
                        {
                            //user edit text not change image
                            updatePost(txtMessage.getText().toString(),imageURL , postId);
                            mRrpcessNotify = false;
                        }
                    }

                }

            }
        });
    }

    /*
    post to firebase database
    wait image url from fire storage
     */
    private void updatePost(String body ,String uri,String key)
    {
        mRefPost = FirebaseDatabase.getInstance().getReference();

        Calendar calendar =  Calendar.getInstance();

        SimpleDateFormat sDate = new SimpleDateFormat("dd/mm/yyyy");
        SimpleDateFormat sTime = new SimpleDateFormat("HH:mm:ss a");

        String time = sTime.format(calendar.getTime());
        String date = sDate.format(calendar.getTime());

        Map<String,Object> map = new HashMap<>();
        map.put("uid",mAuth.getUid());
        map.put("name",userName);
        map.put("body",body);
        map.put("image",uri);
        map.put("status_post","public");
        map.put("date",date);
        map.put("time",time);
        map.put("like","0");
        map.put("postId",key);
        map.put("totalComment","0");

        mRefPost.child("Post").child(key).updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            imageContent.setVisibility(View.GONE);

                            if (mRrpcessNotify)
                            {
                                //
                                Log.i("user post","Successful..");
                                sendNotify(userId , key,"POST" ,body);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext() ,"Post Successful..",Toast.LENGTH_LONG).show();
                                txtMessage.setText("");
                                finish();
                            }
                        }
                    }
                });
    }

    private void sendNotify(String userId, String postId,String topic,String body)
    {

        try
        {

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("postId",postId);
            jsonBody.put("to",userId);
            jsonBody.put("name",userName);
            jsonBody.put("message",""+body);
            jsonBody.put("subtitle","Messages");
            jsonBody.put("notifyType","post");

            JSONObject jsonDetail = new JSONObject();
            jsonDetail.put("to","/topics/"+topic);
            jsonDetail.put("data",jsonBody);

            //
            JsonObjectRequest request = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                    jsonDetail,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("send notify message","Successful.. :"+response.toString());

                            saveNotify(userId,postId,"new posts",body);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("send notify message","error:"+error.toString());
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> map = new HashMap<>();
                    map.put("Content-Type","application/json");
                    map.put("Authorization","key=AAAAiJ74zn8:APA91bERviGp7uwwZgRdPE9a75gxPEMcnmvBP81i2aqp2xtazfdbFMi-crSvDPmN43EYqbExZ2EdgrHjksb_M9zvh3oOhvY5mbBis4vdU2FwlTAx4EUYB1tUA7YC9TF_nuHhGNErzTdW");

                    return map;
                }
            };

            //
            Volley.newRequestQueue(getApplicationContext()).add(request);

        }
        catch (Exception e)
        {
            Log.w("send notify",""+e.getMessage().toString());
        }
//
//        //
//        Map<String,Object> mapBody = new HashMap<>();
//        mapBody.put("postId",postId);
//        mapBody.put("to",userId);
//        mapBody.put("name",userName);
//        mapBody.put("message","new post");
//        mapBody.put("subtitle","Messages");
//        mapBody.put("notifyType","post");
//
//        Map<String,Object> mapDetail = new HashMap<>();
//        mapBody.put("to","/topics/"+topic);
//        mapBody.put("data",mapBody);
//
//        NotifyMessageAPI messageAPI = new NotifyMessageAPI();
//
//        Call<ResponseBody> call = messageAPI.getApi().sendNotifyMessage(mapDetail);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful())
//                {
//                    Log.i("send notify message","Successful.. :");
//                }
//                else
//                {
//                    Log.w("send notify message","Error :"+response.message().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.w("send notify message","Error :"+t.getMessage());
//            }
//        });
    }

    private void saveNotify(String userId, String postId, String new_posts,String body)
    {
        mRefNotify = FirebaseDatabase.getInstance().getReference().child("Notifications");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sDate = new SimpleDateFormat("dd/mm/yyy");
        SimpleDateFormat sTime = new SimpleDateFormat("hh:mm:ss a");

        String date = sDate.format(calendar.getTime());
        String time = sTime.format(calendar.getTime());


        Map<String,Object> map = new HashMap<>();
        map.put("uid",userId);
        map.put("postId",postId);
        map.put("message",body);
        map.put("date",date);
        map.put("time",time);

        mRefNotify.child(mRefNotify.push().getKey()).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext() ,"Post Successful..",Toast.LENGTH_LONG).show();
                            txtMessage.setText("");
                            finish();
                        }
                    }
                });
    }


    //from gallery and camera
    private void clickImage()
    {
       btnOpenImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
             if (imageURL == null)
             {
                 //new post
                 mProcessImage = false;
                 openImage();
             }
             else
             {
                 // edit post
                 mProcessImage = true;
                 openImage();
             }
           }
       });
    }

    private void openImage()
    {
        //Toast.makeText(getApplicationContext() , "image",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            selecteImageUri = data.getData();
            Log.i("image laod",selecteImageUri.toString());
            imageContent.setVisibility(View.VISIBLE);
            //
            imageContent.setImageURI(selecteImageUri);
        }
        else
        {
            Log.i("image laod","error");
        }
    }

    //di
    private void settingDialog(String title , String message)
    {
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }
}
