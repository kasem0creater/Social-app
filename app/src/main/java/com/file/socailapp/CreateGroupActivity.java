package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference sRef;

    //text
    private EditText txtTitle ,txtDescription;

    //image
    private CircularImageView imageGroup;

    //button
    private FloatingActionButton btnCreateGroup;

    //permission camera nad image
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    //
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    //permission array
    private String[] permissionCAMERA;
    private String[] permissionGALLERY;

    //image select image uri
    private Uri imageURi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initialById();

        //initial permission
        permissionCAMERA = new String[]{Manifest.permission.CAMERA ,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        permissionGALLERY = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //imageProfile click
        clickImageGroup();


        //image click
        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
        createGroup();

    }

    private void createGroup()
    {
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRef = FirebaseDatabase.getInstance().getReference().child("Groups");
                String key = mRef.push().getKey();

                if (imageURi != null)
                {


                    //save image to storage
                    sRef = FirebaseStorage.getInstance().getReference().child("groupsImage");
                    String path = "icons/"+key+".jpg";
                    sRef.child(path).putFile(imageURi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful())
                                        {
                                            String imageURL = task.getResult().toString();

                                            //save group to db
                                            String title = txtTitle.getText().toString();
                                            String content = txtDescription.getText().toString();

                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat sDate = new SimpleDateFormat("mm/dd/yyy");
                                            SimpleDateFormat sTime = new SimpleDateFormat("hh:mm:ss a");

                                            String date = sDate.format(calendar.getTime());
                                            String time = sTime.format(calendar.getTime());

                                            Map<String , Object> mapParticipants = new HashMap<>();
                                            mapParticipants.put("role","creator");
                                            mapParticipants.put("uid",mAuth.getUid());

                                            Map<String,Object> mapKey = new HashMap<>();
                                            mapKey.put(mAuth.getUid() , mapParticipants);


                                            Map<String,Object> map = new HashMap<>();
                                            map.put("groupId",key);
                                            map.put("groupTitle",title);
                                            map.put("groupDescription",content);
                                            map.put("groupIcon",""+imageURL);
                                            map.put("dateTime",""+date+" :"+time);
                                            map.put("createBy",mAuth.getUid());
                                            map.put("participants" ,mapKey);

                                            mRef.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        txtDescription.setText("");
                                                        txtTitle.setText("");

                                                        //   imageGroup.setBackgroundResource();
                                                        Toast.makeText(getApplicationContext() ,"create group Successful..." , Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext() ,"create group error" , Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Log.w("upload image",task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else
                {
                    //String imageURL = task.getResult().toString();

                    //save group to db
                    String title = txtTitle.getText().toString();
                    String content = txtDescription.getText().toString();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sDate = new SimpleDateFormat("mm/dd/yyy");
                    SimpleDateFormat sTime = new SimpleDateFormat("hh:mm:ss a");

                    String date = sDate.format(calendar.getTime());
                    String time = sTime.format(calendar.getTime());

                    Map<String , Object> mapParticipants = new HashMap<>();
                    mapParticipants.put("role","creator");
                    mapParticipants.put("uid",mAuth.getUid());

                    Map<String,Object> mapKey = new HashMap<>();
                    mapKey.put(mAuth.getUid() , mapParticipants);


                    Map<String,Object> map = new HashMap<>();
                    map.put("groupId",key);
                    map.put("groupTitle",title);
                    map.put("groupDescription",content);
                    map.put("groupIcon","");
                    map.put("dateTime",""+date+" :"+time);
                    map.put("createBy",mAuth.getUid());
                    map.put("participants" ,mapKey);



                    mRef.child(key).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        txtDescription.setText("");
                                        txtTitle.setText("");

                                        //   imageGroup.setBackgroundResource();
                                        Toast.makeText(getApplicationContext() ,"create group Successful..." , Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext() ,"create group error" , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initialById()
    {
        //firebase
        mAuth = FirebaseAuth.getInstance();

        //image
        imageGroup = findViewById(R.id.image_group);
        //text
        txtTitle = findViewById(R.id.txt_group_title);
        txtDescription = findViewById(R.id.txt_group_descrip);

        //button
        btnCreateGroup = findViewById(R.id.btn_create_group);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null)
        {

        }
    }


    //show popup select image from gallery or camera
    private void clickImageGroup()
    {
       btnCreateGroup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               PopupMenu menu = new PopupMenu(getApplicationContext() , btnCreateGroup);
               MenuInflater  menuInflater = menu.getMenuInflater();

               menuInflater.inflate(R.menu.menu_camera , menu.getMenu());
               //
               menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem menuItem) {
                       int key = menuItem.getItemId();

                       switch (key)
                       {
                           case R.id.popup_camera:
                               if (!checkCameraPermission())
                               {
                                   requestCameraPermission();
                               }
                               else
                               {
                                   pickFromCamera();
                               }
                               break;
                           case R.id.popup_gallery:
                               if (!checkStoragePermission())
                               {
                                   requestStoragePermission();
                               }
                               else
                               {
                                   pickFromGallery();
                               }
                               break;
                       }
                       return false;
                   }
               });

               menu.show();
           }
       });
    }

    private void pickFromCamera()
    {
        //
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE ,"group image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"group description");
        imageURi = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT , imageURi);
        startActivityForResult(intent , IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this , permissionGALLERY ,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result = ActivityCompat.checkSelfPermission(this ,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ActivityCompat.checkSelfPermission(this ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & result1;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this ,
                permissionCAMERA , CAMERA_REQUEST_CODE);
    }

    private void pickFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0)
                {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccept & storageAccept)
                    {
                        pickFromCamera();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext() ,"camera error" , Toast.LENGTH_LONG).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:
            {
                if (grantResults.length > 0)
                {
                    boolean storageAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccept)
                    {
                        pickFromGallery();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext() ,"gallery error" , Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            imageURi = data.getData();
            imageGroup.setImageURI(imageURi);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
