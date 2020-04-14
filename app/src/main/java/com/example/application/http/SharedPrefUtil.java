package com.example.application.http;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.constraintlayout.widget.ConstraintLayout;

/*
储存登录信息
 */
public class SharedPrefUtil {

    private static final String FILE_NAME = "share_date";
    public static final String LOGIN_DATA="loginData";           //用户名
    public static final String REAL_NAME="realName";             //用户真实姓名
    public static final String SEX="sex";                     //用户性别
    public static final String LOCAL="local";                // 用户地址
    public static final String IS_LOGIN="isLogin";
    public static final String FACE_STATE="state";             //人脸是否注册的状态
    public static String USER_NAME;

    /**
     * save data into FILE_NAME ,this path is data/data/POCKET_NAME/shared_prefs
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context , String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.apply();
    }


    /**
     * get value via enter key
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    /**
     * delete key
     * @param context
     * @param key
     */
    public static void removeParam(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String getUserName(Context context){
        String name;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        name = sp.getString(LOGIN_DATA, null);
        return name;
    }

    public static  boolean getFaceState(Context context){                  //获取人脸是否注册信息
        boolean state;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        state = sp.getBoolean(FACE_STATE, false);
        return state;
    }

    public static String getRealName(Context context){              //获取真实姓名
        String name;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        name = sp.getString(REAL_NAME, null);
        return name;
    }

    public static int getSex(Context context){                   //获取性别信息
        int name;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        name = sp.getInt(SEX, 0);
        return name;
    }

    public static String getLocal(Context context){                 //获取位置信息
        String name;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        name = sp.getString(LOCAL, null);
        return name;
    }
}
