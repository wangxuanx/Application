package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.dashboard.LeaveActivity;
import com.example.application.ui.home.group.GroupActivity;
import com.github.leondevlifelog.gesturelockview.GestureLockView;

import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignInActivity extends AppCompatActivity {
    /**主要实现人脸签到与手势签到功能*/
    private GestureLockView gestureLockView;
    private LinearLayout linearLayout;
    private HandsView handsView;
    private EditText editText;
    private TextView timeView;
    private Button button;

    private String password;
    private long time;
    private int hour;
    private int minute;
    private int second;
    private final int FACE = 100;
    private final int HANDS = 101;

    private Intent intent;


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

        linearLayout.setOnClickListener(new View.OnClickListener() {            //时间选择器
            @Override
            public void onClick(View view) {
                Calendar startDate = Calendar.getInstance();
                startDate.set(00, 00, 00, 00,00 ,00);
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(SignInActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                        timeView.setText(longToDate(date.getTime()));
                        time = date.getTime();

                        hour = date.getHours();
                        minute = date.getMinutes();
                        second = date.getSeconds();

                        System.out.println(date);
                    }
                }).setTitleText("签到时长")
                        .setDate(startDate)
                        .setType(new boolean[]{false, false, false, true, true, true})      // 默认全部显示
                        .setSubmitColor(Color.rgb(00,85,77))
                        .setCancelColor(Color.rgb(00,85,77))
                        .build();

                pvTime.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {          //点击发布
            @Override
            public void onClick(View view) {
                String Title = editText.getText().toString().trim();        //获取签到的名称

                intent = new Intent();
                intent.putExtra("name", Title);
                intent.putExtra("time", time);
                intent.putExtra("hour", hour);
                intent.putExtra("minute", minute);
                intent.putExtra("second", second);

                ConnectServer(type, Title, hour, minute, second, password, group);

            }
        });
    }

    private void init(){
        gestureLockView = findViewById(R.id.customGestureLockView);
        handsView = findViewById(R.id.hands_view);
        editText = findViewById(R.id.Sign_in_name);
        linearLayout = findViewById(R.id.sign_time_layout);
        timeView = findViewById(R.id.sign_time);
        button = findViewById(R.id.confirm_Sign_in);
    }

    private void ConnectServer(int type, String title, int hour, int minute, int second, String password, String group){             //向服务器发送数据
        String createName = SharedPrefUtil.getUserName(this);
        String url = "https://120.26.172.16:8443/AndroidTest/CreatSign?type="+type+"&title="+title+"&hour="+hour+"&minutes="+minute+"&second="+second+"&password="+password+"&group="+group+"&creatUser="+createName;
        System.out.println(url);
        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                Log.i("log", "success");
                setResult(0, intent);       //成功
                finish();
            }

            @Override
            public void onFail(Exception e) {
                Log.i("log", "failed");
                setResult(1, getIntent());       //失败
            }
        });
    }

    private static String longToDate(long lo){           //long转化为时形式

        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");         //"yyyy-MM-dd HH:mm:ss"

        return sd.format(date);
    }
}
