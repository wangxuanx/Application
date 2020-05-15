package com.example.application.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.face.utils.Md5;
import com.example.application.http.HttpUtil;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.Register.RegisterActivity;
import com.example.application.ui.home.utils.GenerateTestUserSig;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;

public class LoginActivity extends AppCompatActivity {
    public EditText Username;
    public EditText Password;
    public Button Login;
    public Button Register;
    public CheckBox checkBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        Login = findViewById(R.id.login);
        Register = findViewById(R.id.register);
        checkBox = findViewById(R.id.checkbox);

        Editable username = Username.getText();
        Editable password = Password.getText();
        checkBox.setChecked(true);

        boolean isLogin = (Boolean) SharedPrefUtil.getParam(LoginActivity.this, SharedPrefUtil.IS_LOGIN, false);       //从本地文件判断是否有登录用户

        if (isLogin == true) {       //直接跳转界面
            OpenApp();
        } else {
            Login.setOnClickListener(new View.OnClickListener() {     //登录按钮事件
                @Override
                public void onClick(View view) {
                    //Toast.makeText(LoginActivity.this,username+".."+password,Toast.LENGTH_LONG).show();
                    System.out.println(username + " 测试 " + password);
                    System.out.println(Md5.MD5(password.toString().trim(), "utf-8"));        //加密密码送往服务器

                    /**登录腾讯IM*/
                    TIMManager.getInstance().login(username.toString().trim(), GenerateTestUserSig.genTestUserSig(username.toString().trim()), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            //错误码 code 和错误描述 desc，可用于定位请求失败原因
                            //错误码 code 列表请参见错误码表
                            Log.d("failed", "login failed. code: " + i + " errmsg: " + s);
                        }

                        @Override
                        public void onSuccess() {
                            System.out.println(GenerateTestUserSig.genTestUserSig("wangxuan"));
                            Log.d("login success", "登录成功");
                        }
                    });

                    String path = "https://120.26.172.16:8443/AndroidTest/loginUser?username=" + username.toString() + "&password=" + Md5.MD5(password.toString().trim(), "utf-8");
                    try {
                        HttpsUtil.getInstance().get(path, new HttpsUtil.OnRequestCallBack() {
                            @Override
                            public void onSuccess(final String s) {
                                System.out.println(s);
                                Gson gson = new Gson();
                                UserInfoBean userInfoBean = gson.fromJson(s, UserInfoBean.class);        //解析json数据
                                String msg = userInfoBean.getMsg();

                                if (msg.equals("login successfully")) {
                                    if (checkBox.isChecked()) {           //选择记住登录信息
                                        SharedPrefUtil.setParam(LoginActivity.this, SharedPrefUtil.IS_LOGIN, true);       //保存登录状态
                                        SharedPrefUtil.setParam(LoginActivity.this, SharedPrefUtil.LOGIN_DATA, username.toString());     //保存用户名
                                        SharedPrefUtil.setParam(LoginActivity.this, SharedPrefUtil.REAL_NAME, userInfoBean.getReal_name());      //保存真实姓名
                                        SharedPrefUtil.setParam(LoginActivity.this, SharedPrefUtil.SEX, userInfoBean.getSex());           //保存用户性别
                                        SharedPrefUtil.setParam(LoginActivity.this, SharedPrefUtil.LOCAL, userInfoBean.getLocal());        //保存用户地址
                                    }

                                    OpenApp();
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "密码或用户名输入错误！！！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "登录失败！请检查网络后重试！", Toast.LENGTH_SHORT).show();
                            }
                        });      //发送请求并返回结果

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        }


        Register.setOnClickListener(new View.OnClickListener() {        //注册按钮事件
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void OpenApp() {       //启动app

        /**
         * 已经登录，直接登录IM
         * */
        System.out.println("登录TIM");
        String username = SharedPrefUtil.getUserName(getApplicationContext());
        System.out.println(username);
        TIMManager.getInstance().login(username, GenerateTestUserSig.genTestUserSig(username), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d("failed", "login failed. code: " + i + " errmsg: " + s);
            }

            @Override
            public void onSuccess() {
                System.out.println(GenerateTestUserSig.genTestUserSig("wangxuan"));
                Log.d("login success", "登录成功");
            }
        });

        try {
            Thread.sleep(600);
        } catch (Exception e){
            e.printStackTrace();
        }


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
