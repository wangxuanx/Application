package com.example.application.ui.home.group;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.application.R;
import com.github.leondevlifelog.gesturelockview.GestureLockView;

public class HandsSignActivity extends AppCompatActivity {
    private GestureLockView gestureLockView;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hands_sign);

        setTitle("手势签到");

        init();      //初始化控件

        password = getIntent().getStringExtra("password");        //获取手势签到密码

        gestureLockView.setOnCheckPasswordListener(new GestureLockView.OnCheckPasswordListener() {
            @Override
            public boolean onCheckPassword(String passwd) {
                if (password.equals(passwd)){

                    setResult(RESULT_OK);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
    }

    private void init(){
        gestureLockView = findViewById(R.id.hands_sign_view);
    }
}
