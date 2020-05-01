package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.DialogCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private CardView cardLogin,cardContainer;
    private TextInputEditText txtEmail , txtPassword;
    private Button btnLogin , btnRegister;
    private TextView txtResetPassword;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //string keep deivce
    private String tokenDevice;

    //dialog
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppThemeLogin);

        setContentView(R.layout.activity_login);

        initialById();

        settingCardBackground();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //todo send register
        toRegister();

    }


    private void initialById()
    {
        cardLogin = findViewById(R.id.card_tile_login);
        cardContainer = findViewById(R.id.card_container_view_login);

        //bind id text
        txtEmail = findViewById(R.id.txt_user_login);
        txtPassword = findViewById(R.id.txt_password_login);
        txtResetPassword =findViewById(R.id.txt_reset_password_login);

        //btn
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_to_register_login);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        //dialog
        dialog = new  ProgressDialog(this);

        //reset pass
        resetPassword();

    }

    private void settingCardBackground()
    {
        cardLogin.setBackgroundResource(R.drawable.card_background_title);
        //card container view
        cardContainer.setBackgroundResource(R.drawable.background_radius_login);
    }

    private void userLogin()
    {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        //Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if(email.isEmpty())
        {
            //todo
            txtEmail.setText("valid email");
        }
        else if(password.length() <= 0)
        {
            txtPassword.setText("");
        }
        else
        {
            showMessageBox("Login","Loading User Login...");

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        dialog.dismiss();
                        //
                        writeOnlineStatus("online");
                        updateDeviceToken();

                        startActivity(new Intent(getApplicationContext() ,MainActivity.class));
                    }
                }
            });
        }
    }

    private void updateDeviceToken()
    {
        tokenDevice = FirebaseInstanceId.getInstance().getToken();
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(mAuth.getUid()).child("DeviceToken").setValue(tokenDevice)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Log.i("get device token ","successful..");
                        }
                    }
                });
    }

    private void showMessageBox(String title , String message)
    {
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    private void toRegister()
    {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext() , RegisterActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        if(mAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        }

        super.onStart();

    }

    @Override
    protected void onPause() {

       if (mAuth.getCurrentUser() != null)
       {
           writeOnlineStatus("offline");
       }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAuth.getCurrentUser() != null)
        {
            writeOnlineStatus("offline");
        }
        super.onDestroy();
    }

    /*
            reset pass user
            use email
             */
    private void resetPassword()
    {
        txtResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtEmail.getText().toString().isEmpty())
                {
                    txtEmail.setText("Enter Email for reset");
                }
                else
                {
                    //todo reset
                    mAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.i("Reset Pass","Send new pass to email...");
                            }
                            else
                            {
                                Log.w("Error reset pass",task.getException().getMessage().toString());
                            }
                        }
                    });
                }
            }
        });
    }


    //write user online status and offline
    private void writeOnlineStatus(String status)
    {
        mRef = FirebaseDatabase.getInstance().getReference().child("UserInfo/Data");
        mRef.child(mAuth.getUid()).child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful())
           {
               Log.i("write status","Successful...");
           }
           else
           {
               Log.w("write status","Error...");
           }
            }
        });
    }

}
