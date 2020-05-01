package com.file.socailapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ViewHolderComment extends RecyclerView.ViewHolder
{
    CircularImageView imageprofile;
    TextView txtComment,txtTime;
    ImageView imageComment;

    public ViewHolderComment(@NonNull View itemView) {
        super(itemView);

        imageprofile = itemView.findViewById(R.id.image_circle_profile_comment_list);
        imageComment = itemView.findViewById(R.id.image_comment);

        txtComment = itemView.findViewById(R.id.txt_comment);
        txtTime = itemView.findViewById(R.id.txt_comment_time);

    }
}
