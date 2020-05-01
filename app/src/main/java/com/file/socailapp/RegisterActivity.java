package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText txtName , txtEmail , txtPassword , txtPasswordF;
    private Button btnRegister,btnToLogin;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //dialog
    private ProgressDialog dialog;

    //card view
    private CardView cardContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppThemeRegister);

        setContentView(R.layout.activity_register);

        initialById();


        //
       btnRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               registerUser();
           }
       });

       toLogin();


       //setting background card
        settingCardBackground();

    }

    private void initialById()
    {
        txtName = findViewById(R.id.txt_user_register);
        txtEmail = findViewById(R.id.txt_email_register);
        txtPassword = findViewById(R.id.txt_password_register);
        txtPasswordF = findViewById(R.id.txt_password_confrim_register);

        //btn
        btnRegister = findViewById(R.id.btn_register);
        btnToLogin = findViewById(R.id.btn_to_login_register);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        //dialog
        dialog = new ProgressDialog(this);

        //card
        cardContainer = findViewById(R.id.card_container_view_register);
    }


    private void registerUser()
    {
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        final String name = txtName.getText().toString();
        final String confrimPassword = txtPasswordF.getText().toString();

        //check text null
        if(email.isEmpty())
        {
            txtEmail.setText("Valid Email");
           // txtEmail.findFocus();
        }
        else if(name.isEmpty())
        {
            txtName.setText("valid Name");
        }
        else if(!password.equals(confrimPassword))
        {
            txtPasswordF.setText("");
            txtPassword.setText("valid password");
        }
        else
        {
            showMessageBox("Register User" ,"Loading user Register");
            //todo create user account
            mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener
                    (this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Register Successful...",Toast.LENGTH_LONG).show();

                        //
                        saveUserInfo(name , email);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }

    }

    private void toLogin()
    {
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext() , LoginActivity.class));
            }
        });
    }

    /*
   save user info to db
    -name
    -email
    -nick name
    and other
    */
    private void saveUserInfo(String name , String email)
    {
        //get instance
        String tokenDevice = FirebaseInstanceId.getInstance().getToken();

        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        String uid = mAuth.getCurrentUser().getUid().toString();
        UserInfo info = new UserInfo(email ,name,"","",uid,"online",tokenDevice);
        mRef.child("Data").child(uid).setValue(info.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    dialog.dismiss();

                    Log.i("Create user","Successful...");

                    startActivity(new Intent(getApplicationContext() , MainActivity.class));
                }
            }
        });
    }

    //setting card background
    private void settingCardBackground()
    {
        cardContainer.setBackgroundResource(R.drawable.card_background_corner_register);
    }

    //
    private void showMessageBox(String title , String message)
    {
     dialog.setTitle(title);
     dialog.setMessage(message);
     dialog.show();
    }
}
