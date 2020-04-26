package com.example.application.ui.home.group;

import android.graphics.Bitmap;

public class User {
    private String UserName;
    private int UserHead;
    private String RealName;

    public User(String userName, int userHead, String realName){
        this.UserName = userName;
        this.UserHead = userHead;
        this.RealName = realName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getUserHead() {
        return UserHead;
    }

    public void setUserHead(int userHead) {
        UserHead = userHead;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }
}
