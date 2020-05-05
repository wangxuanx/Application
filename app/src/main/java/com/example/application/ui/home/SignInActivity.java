package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.home.group.GroupActivity;
import com.github.leondevlifelog.gesturelockview.GestureLockView;

import java.awt.font.TextAttribute;

public class SignInActivity extends AppCompatActivity {
    /**主要实现人脸签到与手势签到功能*/
    private GestureLockView gestureLockView;
    private HandsView handsView;
    private EditText editText;
    private EditText hour;
    private EditText minute;
    private EditText second;
    private Button button;

    private String password;
    private final int FACE = 100;
    private final int HANDS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();

        int type = getIntent().getIntExtra("type", FACE);
        String group = getIntent().getStringExtra("group");       //群组名称

        switch (type) {
            case FACE:
                setTitle("人脸签到");
                handsView.setVisibility(View.GONE);
                gestureLockView.setVisibility(View.GONE);
                break;
            case HANDS:
                setTitle("手势签到");
                break;
        }

        gestureLockView.setOnCheckPasswordListener(new GestureLockView.OnCheckPasswordListener() {
            @Override
            public boolean onCheckPassword(String passwod) {
                System.out.println(passwod.charAt(0));
                handsView.setPassword(passwod);
                handsView.invalidate();         //重新绘制
                password = passwod;
                return true;
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {          //点击发布
            @Override
            public void onClick(View view) {
                String Title = editText.getText().toString().trim();        //获取签到的名称
                String hour_String = hour.getText().toString().trim();
                String minute_String = minute.getText().toString().trim();
                String second_String = second.getText().toString().trim();
                int hour_num;
                int minute_num;
                int second_num;
                if(hour_String.equals("")){
                    hour_num = 0;
                } else {
                    hour_num = Integer.parseInt(hour_String);
                }

                if(minute_String.equals("")){
                    minute_num = 0;
                } else {
                    minute_num = Integer.parseInt(minute_String);
                }

                if(second_String.equals("")){
                    second_num = 0;
                } else {
                    second_num = Integer.parseInt(second_String);
                }

                Intent intent = new Intent();
                intent.putExtra("name", Title);
                intent.putExtra("hour", hour_num);
                intent.putExtra("minute", minute_num);
                intent.putExtra("second", second_num);


                System.out.println(Title+" "+hour_num+" "+minute_num+" "+second_num+" "+password);

                //ConnectServer(type, Title, hour_num, minute_num, second_num, password, group);

                setResult(0, intent);
                finish();
            }
        });
    }

    private void init(){
        gestureLockView = findViewById(R.id.customGestureLockView);
        handsView = findViewById(R.id.hands_view);
        editText = findViewById(R.id.Sign_in_name);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);
        button = findViewById(R.id.confirm_Sign_in);
    }

    private void ConnectServer(int type, String title, int hour, int minute, int second, String password, String group){             //向服务器发送数据
        String createName = SharedPrefUtil.getUserName(this);
        String url = "https://120.26.172.16:8443/AndroidTest/CreatSign?type="+type+"&title="+title+"&hour="+hour+"&minutes="+minute+"&second="+second+"&password="+password+"&group="+group+"&creatUser="+createName;
        System.out.println(url);
        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                setResult(0, getIntent());       //成功
            }

            @Override
            public void onFail(Exception e) {
                setResult(1, getIntent());       //失败
            }
        });
    }
}
