package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.application.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();

        int type = getIntent().getIntExtra("type", 100);

        switch (type) {
            case 100:
                setTitle("人脸签到");
                handsView.setVisibility(View.GONE);
                gestureLockView.setVisibility(View.GONE);
                break;
            case 101:
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

                System.out.println(Title+" "+hour_num+" "+minute_num+" "+second_num+" "+password);

                ConnectServer(Title, hour_num, minute_num, second_num, password);

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

    private void ConnectServer(String title, int hour, int minute, int second, String password){             //向服务器发送数据

    }
}
