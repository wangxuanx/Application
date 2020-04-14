package com.example.application.ui.home;

/**
 * 群组信息类
 * */

public class Group {
    private int ImageID;
    private String GroupName;
    private String GroupDescribe;

    public Group(int ImageID, String GroupName, String GroupDescribe){
        this.ImageID = ImageID;
        this.GroupName = GroupName;
        this.GroupDescribe = GroupDescribe;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }
    public int getImageID() {
        return ImageID;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
    public String getGroupName() {
        return GroupName;
    }

    public void setGroupDescribe(String groupDescribe) {
        GroupDescribe = groupDescribe;
    }
    public String getGroupDescribe() {
        return GroupDescribe;
    }
}
