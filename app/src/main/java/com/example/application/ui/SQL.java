package com.example.application.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.ui.home.group.Group;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQL {
    /***
     * 本地sql语句类
     */

    public static final String default_sql = "CREATE TABLE IF NOT EXISTS group_list (id integer primary key)";
    public static final String sql_create_group_list = "CREATE TABLE IF NOT EXISTS group_list (id integer primary key, group_id varchar(255) not null, group_name varchar(255) not null, last_message varchar(255))";
    public static final String sql_create_sign_list = "CREATE TABLE IF NOT EXISTS sign_list (id integer primary key, title varchar(255) not null, type varchar(5) not null, deadline_time integer not null, password varchar(10) not null, groupName varchar(255) not null, createUser varchar(255) not null, state int not null)";
    public static final String sql_create_leave_list = "CREATE TABLE IF NOT EXISTS leave_list (id integer primary key, title varchar(255) not null, type varchar(255) not null, user varchar(255) not null, beginTime datetime not null, endTime datetime not null, otherInfo varchar(255), state varchar(20) not null)";


    public static String getChatSql(String title){                 //创建聊天信息列表
        String SQL = "CREATE TABLE IF NOT EXISTS "+title
                +"_group_chat_list ("
                +"id integer primary key, "
                +"seq varchar(50) not null, "
                +"text varchar(255) not null, "
                +"user varchar(255) not null, "
                +"type int(2) not null)";

        return SQL;
    }

    public static String getCheckSql(String name) {           //获取签到的sql
        String sql = "CREATE TABLE IF NOT EXISTS "+name
                +"_check_user_list ("
                +"id integer primary key, "
                +"userName varchar(255) not null, "
                +"realName varchar(255) not null)";
        return sql;
    }

    public static String formatLongToTimeStr(Long l) {
        Date date = new Date(l);

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sd.format(date);
        /*int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue() ;
        if (second > 60) {
            minute = second / 60;   //取整
            second = second % 60;   //取余
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour+":"+minute+":"+second;
        return strtime;*/
    }

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

    public static String getGroupName(Context context, String groupID){
        String name = "";

        DatabaseHelper databaseHelper = new DatabaseHelper(context, "group_list", null, 1, sql_create_group_list);       //向数据库插入数据
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //databaseHelper.CreateTable(db);
        Cursor cursor = db.query("group_list", null, "group_id = ?", new String[]{groupID}, null, null, "id");

        int i = cursor.getCount();
        if (i != 0){
            cursor.moveToLast();
            System.out.println(cursor);
            name = cursor.getString(2);
        }
        cursor.close();
        db.close();
        databaseHelper.close();

        return name;
    }
}
