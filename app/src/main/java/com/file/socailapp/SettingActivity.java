package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat switchNotify;

    //shared references
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //String subscribe notify
    private static String SUBSCRIBE_TOPIC="POST";
    private static String UNSUBSCRIBE_TOPIC="UN_POST";

    private boolean isPostEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initialById();

        switchChange();

        //
        isPostEnabled = preferences.getBoolean(""+SUBSCRIBE_TOPIC,false);

        if (isPostEnabled)
        {
            switchNotify.setChecked(true);
        }
        else
        {
            switchNotify.setChecked(false);
        }
    }

    //switch change enable
    // -> send notify
    //disable -> not send
    private void switchChange()
    {
        switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor = preferences.edit();
                editor.putBoolean(""+SUBSCRIBE_TOPIC,b);
                editor.apply();


                if (b)
                {
                    //todo enabled
                    subscribePostNotify();
                }
                else
                {
                    //todo disabled
                    unSubscribePostNotify();
                }
            }
        });
    }

    private void subscribePostNotify()
    {
        FirebaseMessaging.getInstance().subscribeToTopic(""+SUBSCRIBE_TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String message = "you will receiver notify new post";
                        if (!task.isSuccessful())
                        {
                            message="subscribe failed";
                        }
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void unSubscribePostNotify()
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+SUBSCRIBE_TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String message = "you will not receiver notify new post";
                        if (!task.isSuccessful())
                        {
                            message="Unsubscribe failed";
                        }
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }
                });
    }

    // by id view
    private void initialById()
    {
        switchNotify = findViewById(R.id.post_switch);

        //shared preferences
        preferences = getSharedPreferences("POST_NOTIFY_SYS", Context.MODE_PRIVATE);
    }
}
