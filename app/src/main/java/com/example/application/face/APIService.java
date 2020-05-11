package com.example.application.face;

import android.util.Base64;

import com.example.application.face.utils.GsonUtils;
import com.example.application.face.utils.Md5;
import com.example.application.http.HttpUtil;
import com.example.application.http.SharedPrefUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.example.application.face.utils.Base64RequestBody.readFile;

public class APIService {

    private static volatile APIService instance;

    public static APIService getInstance() {
        if (instance == null) {
            synchronized (APIService.class) {
                if (instance == null) {
                    instance = new APIService();


                }
            }
        }
        return instance;
    }

    /**
    注册人脸
     */
    public static void RegFace(File file, String user_id){
        String BASE64Img;         //图片base64值
        String userID = user_id;          //用户id
        try {
            byte[] buf = readFile(file);
            BASE64Img = new String(Base64.encode(buf, Base64.NO_WRAP));      //转化为BSAE64、
            System.out.println("人脸图片BASE64:");
            System.out.println(BASE64Img);

            new Thread(){
                @Override
                public void run() {
                    try{
                        String url = "https://120.26.172.16:8443/AndroidTest/faceRegist";
                        String Content_Type	= "application/json";
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", BASE64Img);
                        map.put("user_id", user_id);

                        String body = GsonUtils.toJson(map);

                        String response = HttpUtil.post(url, Content_Type, body);      //发送请求并返回结果
                        System.out.println("返回结果：");
                        System.out.println(response);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    更新用户信息
     */
    public static void updateUserInfo(int type, String info, String username){
        new Thread(){
            @Override
            public void run() {
                try {
                    String path="https://120.26.172.16:8443/AndroidTest/updateUserInfo";
                    String Content_Type	= "application/json";
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", type);
                    map.put("info", info);
                    map.put("username", username);

                    String body = GsonUtils.toJson(map);

                    String response = HttpUtil.post(path, Content_Type, body);      //发送请求并返回结果
                    System.out.println("返回结果：");
                    System.out.println(response);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 搜索人脸信息
     * */

    public static void SearchFace(File file, String user_id){
        String BASE64Img;         //图片base64值
        String userID = user_id;          //用户id
        try {
            byte[] buf = readFile(file);
            BASE64Img = new String(Base64.encode(buf, Base64.NO_WRAP));      //转化为BSAE64、
            System.out.println("人脸图片BASE64:");
            System.out.println(BASE64Img);

            new Thread(){
                @Override
                public void run() {
                    try{
                        String url = "https://120.26.172.16:8443/AndroidTest/SearchFace";
                        String Content_Type	= "application/json";
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", BASE64Img);
                        map.put("user_id", user_id);

                        String body = GsonUtils.toJson(map);

                        String response = HttpUtil.post(url, Content_Type, body);      //发送请求并返回结果
                        System.out.println("返回结果：");
                        System.out.println(response);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
