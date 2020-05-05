package com.example.application.ui.dashboard;

public class CheckItemBean {
    private String checkType;
    private String checkUserType;
    private String beginTime;
    private String endTime;
    private String state;

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckUserType(String checkUserType) {
        this.checkUserType = checkUserType;
    }

    public String getCheckUserType() {
        return checkUserType;
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
