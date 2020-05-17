package com.example.application.face;

import android.os.Handler;
import android.util.Base64;

import com.example.application.face.utils.GsonUtils;
import com.example.application.face.utils.Md5;
import com.example.application.http.HttpUtil;
import com.example.application.http.HttpsUtil;
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
    public static String RegFace(File file, String user_id){
        String BASE64Img;         //图片base64值
        String userID = user_id;          //用户id
        final String[] back = new String[1];
        try {
            byte[] buf = readFile(file);
            BASE64Img = new String(Base64.encode(buf, Base64.NO_WRAP));      //转化为BSAE64、

            String url = "https://120.26.172.16:8443/AndroidTest/faceRegist";
            Map<String, Object> map = new HashMap<>();
            map.put("image", BASE64Img);
            map.put("user_id", userID);

            String body = GsonUtils.toJson(map);

            android.os.Handler handler = new Handler();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        String response = HttpsUtil.getInstance().sendPost(url, body, null);      //发送请求并返回结果
                        back[0] = response;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            });

            thread.start();
            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return back[0];
    }

    /**
    更新用户信息
     */
    public static String updateUserInfo(int type, String info, String username){
        String url="https://120.26.172.16:8443/AndroidTest/updateUserInfo";
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("info", info);
        map.put("username", username);

        String body = GsonUtils.toJson(map);
        final String[] result = new String[1];

        new Thread(){
            @Override
            public void run() {
                try {

                    String response = HttpsUtil.getInstance().sendPost(url, body, null);     //发送请求并返回结果
                    result[0] = response;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result[0];
    }

    /**
     * 搜索人脸信息
     * */

    public static String SearchFace(File file, String user_id){             //判断人脸是否存在
        String BASE64Img;         //图片base64值
        String userID = user_id;          //用户id
        final String[] result = new String[1];      //返回值

        try {
            byte[] buf = readFile(file);
            BASE64Img = new String(Base64.encode(buf, Base64.NO_WRAP));      //转化为BSAE64

            String url = "https://120.26.172.16:8443/AndroidTest/SearchFace";
            String Content_Type	= "application/json";
            Map<String, Object> map = new HashMap<>();
            map.put("image", BASE64Img);
            map.put("user_id", userID);
            String body = GsonUtils.toJson(map);

            new Thread(){
                @Override
                public void run() {
                    try{
                        String response = HttpsUtil.getInstance().sendPost(url, body, null);      //发送请求并返回结果
                        result[0] = response;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

            Thread.sleep(3500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result[0];
    }
}
