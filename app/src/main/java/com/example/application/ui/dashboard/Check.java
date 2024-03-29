package com.example.application.ui.dashboard;

public class Check {
    private int id;
    private String title;          //标题
    private String type;            //类型
    private String otherInfo;
    private String beginTime;         //起始时间
    private String endTime;          //终止时间
    private String state;         //假条状态
    private String belongUser;

    public Check(int ID, String TITLE, String TYPE, String USER, String OTHERINFO, String BEGINTIME, String ENDTIME, String STATE){
        this.id = ID;
        this.title = TITLE;
        this.type = TYPE;
        this.belongUser = USER;
        this.otherInfo = OTHERINFO;
        this.beginTime = BEGINTIME;
        this.endTime = ENDTIME;
        this.state = STATE;
    }

    /***
     * 无参数构造函数
     */
    public  Check(){

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

    public void setUser(String user) {
        this.belongUser = user;
    }

    public String getUser() {
        return belongUser;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getOtherInfo() {
        return otherInfo;
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
