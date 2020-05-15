package com.example.application.ui;

public class SQL {
    /***
     * 本地sql语句类
     */

    public static final String sql_create_group_list = "CREATE TABLE IF NOT EXISTS group_list (id integer primary key, group_id varchar(255) not null, group_name varchar(255) not null, last_message varchar(255))";
    public static final String sql_create_sign_list = "CREATE TABLE IF NOT EXISTS sign_list (id integer primary key, title varchar(255) not null, type varchar(5) not null, deadline_time integer not null, password varchar(10) not null, groupName varchar(255) not null, createUser varchar(255) not null)";

}
