package com.file.socailapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class GroupHolder extends RecyclerView.ViewHolder
{
    CircularImageView imageGroup;
    TextView txtGroupName , txtGroupMessage,txtLastSeenTime;
    ImageButton btnGroupSetting;


    public GroupHolder(@NonNull View itemView) {
        super(itemView);

        imageGroup = itemView.findViewById(R.id.image_group);

        txtGroupName = itemView.findViewById(R.id.txt_group_name);
        txtGroupMessage = itemView.findViewById(R.id.txt_group_message);
        txtLastSeenTime = itemView.findViewById(R.id.txt_group_time);

        btnGroupSetting = itemView.findViewById(R.id.btn_group_setting);
    }
}
