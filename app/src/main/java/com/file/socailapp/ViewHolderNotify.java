package com.file.socailapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ViewHolderNotify extends RecyclerView.ViewHolder
{
    CircularImageView imageProfile;
    TextView txtName,txtNotify;

    public ViewHolderNotify(@NonNull View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.image_group);

        txtName = itemView.findViewById(R.id.txt_user_name);
        txtNotify = itemView.findViewById(R.id.txt_notify_content);
    }
}
