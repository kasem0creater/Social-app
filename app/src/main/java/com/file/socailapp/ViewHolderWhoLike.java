package com.file.socailapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ViewHolderWhoLike extends RecyclerView.ViewHolder
{
    TextView txtName,txtNickName;
    CircularImageView imageProfile,userStatus;
    ImageButton btn_User;

    public ViewHolderWhoLike(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txt_all_user_name);
        txtNickName = itemView.findViewById(R.id.txt_all_user_nick_name);

        imageProfile = itemView.findViewById(R.id.image_group);
        userStatus = itemView.findViewById(R.id.image_status_);

        btn_User = itemView.findViewById(R.id.btn_setting_user);
    }
}
