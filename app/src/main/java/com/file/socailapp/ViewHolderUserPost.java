package com.file.socailapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ViewHolderUserPost extends RecyclerView.ViewHolder
{
    CircularImageView imageprofile;
    TextView txtName ,txtTime , txtContentPost,txtLike, txtComment;
    ImageView imagePostContent;
    ImageButton btnSettingPost;
    Button btnLike , btnComment , btnShared;

    public ViewHolderUserPost(@NonNull View itemView) {
        super(itemView);

        //image
        imageprofile = itemView.findViewById(R.id.image_circle_profile_post_list);
        imagePostContent = itemView.findViewById(R.id.image_post_content_list);

        //text
        txtName = itemView.findViewById(R.id.txt_user_post_list);
        txtTime = itemView.findViewById(R.id.txt_time_post_list);
        txtContentPost = itemView.findViewById(R.id.txt_content_post_list);
        txtLike = itemView.findViewById(R.id.txt_show_like_post_list);
        txtComment = itemView.findViewById(R.id.txt_show_comment_post_list);

        //button
        btnSettingPost = itemView.findViewById(R.id.btn_post_setting_list);
        btnLike = itemView.findViewById(R.id.btn_like_post_list);
        btnComment = itemView.findViewById(R.id.btn_comment_post_list);
        btnShared = itemView.findViewById(R.id.btn_shared_post_list);


    }
}
