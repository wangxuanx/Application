package com.example.application.ui.home.msg;

public class Msg {
    public static final int RECEIVE = 0;
    public static final int SEND = 1;
    private String user;          //发送用户
    private String content;       //消息内容
    private int type;            //接收还是发送

    public Msg(String user, String content, int type){
        this.user = user;
        this.content = content;
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
