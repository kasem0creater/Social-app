package com.file.socailapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ViewHolderReceiver extends RecyclerView.ViewHolder
{
    //text view
    TextView txtSender ,txtFriend;
    //image
    ImageView imageSender , imageFriend;
    CircularImageView profile;

    public ViewHolderReceiver(@NonNull View itemView) {
        super(itemView);
        txtSender = itemView.findViewById(R.id.txt_sender_message);
        txtFriend = itemView.findViewById(R.id.txt_friend_message);

        //
        profile = itemView.findViewById(R.id.image_user_message_layout);

        //
        imageFriend = itemView.findViewById(R.id.image_message_friend);
        imageSender = itemView.findViewById(R.id.image_message_sender);
    }
}
