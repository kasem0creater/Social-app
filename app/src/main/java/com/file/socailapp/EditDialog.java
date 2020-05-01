package com.file.socailapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class EditDialog extends DialogFragment
{
    //text
    private EditText txtName , txtNickName;
    //button
    private ImageButton btnCancel ,btnOk;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //string
    private String email , image ,name , nick,uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.layout_edit_user_detail , container ,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().show();

        //
        initialById(view);

        //load
        loadUserInfo();

        //btn ok click
        onclickUserApply();

        //btn cancel click
        onClickCancel();
    }


    private void initialById(View view)
    {
        txtName = view.findViewById(R.id.txt_edit_name);
        txtNickName = view.findViewById(R.id.txt_edit_nick_name);

        btnOk = view.findViewById(R.id.btn_edit_ok);
        btnCancel = view.findViewById(R.id.btn_edit_cancel);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
    }

    /*
    get user info as to text
     */
    private void loadUserInfo()
    {
       final String uidUser = mAuth.getUid().toString();
       mRef.child(uidUser).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   email = dataSnapshot.child("Email").getValue().toString();
                   name = dataSnapshot.child("Name").getValue().toString();
                   nick = dataSnapshot.child("NickName").getValue().toString();
                   image = dataSnapshot.child("Image").getValue().toString();
                   uid = dataSnapshot.child("Uid").getValue().toString();

                   txtName.setText(name);
                   txtNickName.setText(nick);
               }
               Log.i("get user info","dialog :"+"Successful..");
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.w("Error get user info","dialog :"+databaseError.getMessage().toString());
           }
       });

    }

    //btn ok click
    private void onclickUserApply()
    {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String tokenDevice = FirebaseInstanceId.getInstance().getToken();
                UserInfo info = new UserInfo(email,txtName.getText().toString(),txtNickName.getText().toString(),image,mAuth.getUid().toString(),"online",tokenDevice);

                mRef.child(mAuth.getUid().toString()).setValue(info.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful())
                   {
                       Log.i("update user info","sucessful...");
                       dismiss();
                   }
                   else
                   {
                       Log.w("update user info","error...");
                   }
                    }
                });
            }
        });
    }


    // exit from dialog
    private void onClickCancel() {
       btnCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dismiss();
           }
       });
    }
}
