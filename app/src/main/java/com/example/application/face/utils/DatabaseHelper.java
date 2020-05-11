package com.example.application.face.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String url;        //创建的sql语句
    public SQLiteDatabase db;
    private static DatabaseHelper instance;

    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String url) {
        super(context, name, factory, version);
        this.url = url;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句 并 执行
        this.db = db;
        db.execSQL(url);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * 判断某张表是否存在
     * @paramtabName表名
     * @return
     */
    public boolean tableIsExist(String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master  where type = 'table' and name = '"+tableName+"' ";
            System.out.println(sql);
            //cursor = db.rawQuery(sql, null);
            cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
            while(cursor.moveToNext()){
                //遍历出表名
                String name = cursor.getString(0);
                Log.i("System.out", name);
            }
            /*if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }*/

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }
}
