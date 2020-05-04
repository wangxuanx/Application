package com.example.application.ui.home.group;

/**
 * 群组信息类
 * */

public class Group {
    private int ImageID;
    private String GroupID;
    private String GroupName;
    private String GroupDescribe;

    public Group(int ImageID, String GroupID, String GroupName, String GroupDescribe){
        this.GroupID = GroupID;
        this.ImageID = ImageID;
        this.GroupName = GroupName;
        this.GroupDescribe = GroupDescribe;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getGroupID() {
        return GroupID;
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
