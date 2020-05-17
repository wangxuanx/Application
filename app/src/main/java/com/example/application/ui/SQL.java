package com.example.application.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQL {
    /***
     * 本地sql语句类
     */

    public static final String sql_create_group_list = "CREATE TABLE IF NOT EXISTS group_list (id integer primary key, group_id varchar(255) not null, group_name varchar(255) not null, last_message varchar(255))";
    public static final String sql_create_sign_list = "CREATE TABLE IF NOT EXISTS sign_list (id integer primary key, title varchar(255) not null, type varchar(5) not null, deadline_time integer not null, password varchar(10) not null, groupName varchar(255) not null, createUser varchar(255) not null, state int not null)";
    public static final String sql_create_leave_list = "CREATE TABLE IF NOT EXISTS leave_list (id integer primary key, title varchar(255) not null, type varchar(255) not null, user varchar(255) not null, beginTime datetime not null, endTime datetime not null, otherInfo varchar(255), state varchar(20) not null)";


    public static long DataToLang(String data){
        long time = 0;
        try {
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt2 = sdf.parse(data);

            //继续转换得到秒数的long型
            time = dt2.getTime() / 1000;
        } catch (Exception e){
            e.printStackTrace();
        }


        return time;
    }
}
