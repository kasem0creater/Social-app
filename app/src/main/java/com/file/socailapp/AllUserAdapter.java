package com.file.socailapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllUserAdapter extends RecyclerView.Adapter<ViewHolderAlluser>
{
    private List<UserInfo> info = new ArrayList<>();
    private DatabaseReference mRefUnblock,mRefBlock;
    private FirebaseAuth mAuth;

    private Context context;
    private static String type;
    private String BlockUid;

    public AllUserAdapter(Context context ,List<UserInfo> infoUser ,String type)
    {
        this.info = infoUser;
        this.context = context;
        this.type = type;
    }


    @NonNull
    @Override
    public ViewHolderAlluser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_all_user , parent,false);

        mAuth = FirebaseAuth.getInstance();

        ViewHolderAlluser viewHolderAlluser = new ViewHolderAlluser(v);
        return viewHolderAlluser;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAlluser holder, int position) {

        //check type adapter
        //is chat hi menu setting
        //is list user show menu setting

        if (type.equals("chat"))
        {
            holder.btn_User.setVisibility(View.GONE);
        }
        else
        {
            holder.btn_User.setVisibility(View.VISIBLE);
        }


        if(info.get(position).getStatus().equals("online"))
        {
            //todo user online

            loadBlock(info.get(position).getUid());


            holder.userStatus.setVisibility(View.VISIBLE);

            holder.txtName.setText(info.get(position).getName()+"d");
            holder.txtNickName.setText(info.get(position).getNickName().toString());

            // get image
            String image = info.get(position).getImage()+"";
            if(!image.isEmpty())
            {
                Picasso.get().load(image).into(holder.imageProfile);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                public void onClick(View view) {
                    String current = mAuth.getUid();

                    mRefBlock = FirebaseDatabase.getInstance().getReference()
                            .child("UserInfo/Data");
                    mRefBlock.child(current).child("BlockUser").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(info.get(position).getUid()))
                            {
                                //todo this user as block
                                Toast.makeText(context.getApplicationContext() ,"Block User",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                //todo this user not block
                                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                                intent.putExtra("userName",info.get(position).getName());
                                intent.putExtra("image",info.get(position).getImage());
                                intent.putExtra("uid",info.get(position).getUid()+"");
                                intent.putExtra("status",info.get(position).getStatus());
                                view.getContext().startActivity(intent);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });


            //linear user click show popup
            holder.btn_User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(context.getApplicationContext(),holder.btn_User);
                    menu.getMenuInflater().inflate(R.menu.action_user,menu.getMenu());
                    //
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int key = menuItem.getItemId();
                            switch (key)
                            {
                                case R.id.popup_block:
                                    blockUser(info.get(position).getUid());
                                    break;
                                case R.id.popup_un_block:
                                    unBlockUser(info.get(position).getUid());
                                    break;
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });


        }
        else
        {
            //todo user offline

            //block
            loadBlock(info.get(position).getUid());


            holder.userStatus.setVisibility(View.GONE);

            holder.txtName.setText(info.get(position).getName()+"d");
            holder.txtNickName.setText(info.get(position).getNickName().toString());

            //
            String image = info.get(position).getImage().toString();
            if (image.isEmpty())
            {
            }
            else
            {
                Picasso.get().load(image).into(holder.imageProfile);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String current = mAuth.getUid();

                    mRefBlock = FirebaseDatabase.getInstance().getReference()
                            .child("UserInfo/Data");
                    mRefBlock.child(current).child("BlockUser").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(info.get(position).getUid()))
                            {
                                //todo this user as block
                                Toast.makeText(context.getApplicationContext() ,"Block User",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                //todo this user not block
                                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                                intent.putExtra("userName",info.get(position).getName());
                                intent.putExtra("image",info.get(position).getImage());
                                intent.putExtra("uid",info.get(position).getUid()+"");
                                intent.putExtra("status",info.get(position).getStatus());
                                view.getContext().startActivity(intent);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            //linear user click show popup
            holder.btn_User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu menu = new PopupMenu(context.getApplicationContext(),holder.btn_User);
                    menu.getMenuInflater().inflate(R.menu.action_user,menu.getMenu());
                    //
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int key = menuItem.getItemId();
                            switch (key)
                            {
                                case R.id.popup_block:
                                    blockUser(info.get(position).getUid());
                                    break;
                                case R.id.popup_un_block:
                                    unBlockUser(info.get(position).getUid());
                                    break;
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });

        }


        //

    }

    private boolean mProcessBlock = false;
    private void loadBlock(String uid)
    {
        String current = mAuth.getUid();

        mRefBlock = FirebaseDatabase.getInstance().getReference()
                .child("UserInfo/Data");
        mRefBlock.child(current).child("BlockUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid))
                {
                    //todo this user as block
                    BlockUid = dataSnapshot.getKey();
                }
                else
                {
                    //todo this user not block
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void unBlockUser(String uid)
    {
        String current = mAuth.getUid();

        mRefUnblock = FirebaseDatabase.getInstance().getReference()
                .child("UserInfo/Data").child(current);

        mRefUnblock.child("BlockUser").child(uid).removeValue();
    }

    private void blockUser(String uid)
    {
        String current = mAuth.getUid();

        mRefBlock = FirebaseDatabase.getInstance().getReference()
                .child("UserInfo/Data");

        Map<String , Object> map = new HashMap<>();
        map.put("blockId",mRefBlock.push().getKey());
        map.put("uid",uid);

        mRefBlock.child(current).child("BlockUser").child(uid).setValue(map);
    }

    @Override
    public int getItemCount() {
        return info.size();
    }
}
