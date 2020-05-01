package com.file.socailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    //appbar
    private Toolbar toolbar;
    //firebase
    private static FirebaseAuth mAuth;
    private DatabaseReference mRef;

    //bottom navigator
    private BottomNavigationView bottomMenu;
    //adapter view pager
    private ViewPagerAdapter pager;
    //frame
    private ViewPager frameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();

        //setting app bar
        settingAppBar();

        //bottom navigator click
        onClickBottomNavigator();

        //view pager
        settingViewpager();
    }

    private void initial()
    {
        mAuth = FirebaseAuth.getInstance();

        //bottom navigator
        bottomMenu = findViewById(R.id.bottom_navigator_menu);

        //frame layout
        frameLayout = findViewById(R.id.view_pager_container);

        //view pager
        pager = new ViewPagerAdapter(getSupportFragmentManager());

        //toll bar
        toolbar = findViewById(R.id.custom_toolBar);

    }

    //setting fragment on view pager
    private void settingViewpager()
    {
        frameLayout.setAdapter(pager);

        frameLayout.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                int i = position;
                switch (i)
                {
                    case 0 :
                        toolbar.setTitle("Feed");
                        break;
                    case 1:
                        toolbar.setTitle("User");
                        break;
                    case 2:
                        toolbar.setTitle("Profile");
                        break;
                    case 3:
                        toolbar.setTitle("Chat");
                        break;
                }
                Log.i("pageSelect",""+i);
            }
        });
    }

    //bottom navigator setting
    //click item
    private void onClickBottomNavigator()
    {
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.feed_data:
                      frameLayout.setCurrentItem(0);
                        break;
                    case R.id.friend:
                        frameLayout.setCurrentItem(1);
                        break;
                    case R.id.profile:
                        frameLayout.setCurrentItem(2);
                        break;
                    case R.id.chat:
                        frameLayout.setCurrentItem(3);
                        break;
                    case R.id.menu_more:

                        View view = findViewById(R.id.menu_more);

                        PopupMenu menu = new PopupMenu(getApplicationContext(),view);
                        MenuInflater menuInflater =menu.getMenuInflater();
                        menuInflater.inflate(R.menu.popup_settiing , menu.getMenu());


                        //event
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                int key = menuItem.getItemId();
                                switch (key)
                                {
                                    case R.id.popup_setting:
                                        startActivity(new Intent(getApplicationContext() , SettingActivity.class));
                                        break;
                                    case R.id.popup_notify:
                                        startActivity(new Intent(getApplicationContext() ,NotifyActivity.class));
                                        break;
                                    case R.id.popup_group:
                                        createNewGroup();
                                        break;
                                    case R.id.popup_access_group:
                                        groupInfo();
                                       break;
                                }
                                return false;
                            }
                        });
                        //
                        menu.show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void groupInfo()
    {
        startActivity(new Intent(getApplicationContext() , Groupsctivity.class));
    }

    //create new group chat
    //and add user as group
    private void createNewGroup()
    {
        startActivity(new Intent(getApplicationContext(),CreateGroupActivity.class));
    }

    //setting app bar
    private void settingAppBar()
    {
        getSupportActionBar();
        toolbar.setBackgroundResource(R.color.facebookColor);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main_setting , menu);
        MenuItem searchicon = menu.findItem(R.id.menu_search_user);
        searchicon.setVisible(false);

        return true;
    }

    //

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_setting:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext() , LoginActivity.class));
                break;
            case R.id.menu_post:
                String uid = mAuth.getUid();
                Intent intent = new Intent(getApplicationContext(),PostActivity.class);
                //intent.putExtra("postId",postId);
                intent.putExtra("uid",uid);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


        //mAuth = FirebaseAuth.getInstance();
        //String currentUser = mAuth.getUid().toString();
        if (mAuth.getCurrentUser() == null)
        {
            startActivity(new Intent(this , LoginActivity.class));
        }
        else
        {}

        if(mAuth.getCurrentUser() != null)
        {
            //
            writeOnlineStatus("online");
          //  Log.i("get device token",getIntent().getExtras().getString("message"));
        }
    }

    @Override
    protected void onResume() {
        if (mAuth.getCurrentUser() != null)
        {
            writeOnlineStatus("online");
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        if (mAuth.getCurrentUser() != null)
        {
            writeOnlineStatus("online");
        }
        super.onRestart();
    }

    @Override
    public void onBackPressed() {

        if (mAuth.getCurrentUser() != null)
        {

        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        //
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
