package com.file.socailapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class GroupModel
{
    String createBy;
    String dateTime;
    String groupDescription;
    String groupIcon;
    String groupId;
    String groupTitle;

    public GroupModel()
    {}

    public GroupModel(String createBy, String dateTime, String groupDescription, String groupIcon, String groupId, String groupTitle) {
        this.createBy = createBy;
        this.dateTime = dateTime;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
        this.groupId = groupId;
        this.groupTitle = groupTitle;
    }


    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }






    public String getCreateBy() {
        return createBy;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }


}
