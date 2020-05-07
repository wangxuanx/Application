package com.example.application.ui.dashboard;

public class Check {
    private int id;
    private String title;          //标题
    private String type;            //类型
    private String beginTime;         //起始时间
    private String endTime;          //终止时间
    private String state;         //假条状态

    public Check(int ID, String TITLE, String TYPE, String BEGINTIME, String ENDTIME, String STATE){
        this.id = ID;
        this.title = TITLE;
        this.type = TYPE;
        this.beginTime = BEGINTIME;
        this.endTime = ENDTIME;
        this.state = STATE;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
