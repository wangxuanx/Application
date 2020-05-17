package com.example.application.ui.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.face.APIService;
import com.example.application.http.SharedPrefUtil;

public class SetItemActivity extends Activity {

    protected ImageView imageView;          //返回按钮
    protected TextView textView;            //完成修改按钮
    protected EditText editText;            //修改的信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_item);
        int type = getIntent().getIntExtra("type", 1);
        System.out.println(type);

        String user_name = SharedPrefUtil.getUserName(this);

        init();

        imageView.setOnClickListener(new View.OnClickListener() {          //返回操作
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {             //完成修改更新用户信息
            @Override
            public void onClick(View view) {
                String string = editText.getText().toString().trim();          //获得修改的信息
                if (string.equals("")) {            //为空，不修改
                    finish();
                } else {       //不为空，修改

                    String result = APIService.getInstance().updateUserInfo(type, string, user_name);            //调用api更新信息

                    if (result.equals("update successfully!")){
                        switch (type){          //判断修改的具体内容
                            case 102:         //用户名
                                SharedPrefUtil.setParam(getApplicationContext(), SharedPrefUtil.LOGIN_DATA, string);     //保存用户名
                                break;
                            case 103:          //真实姓名
                                SharedPrefUtil.setParam(getApplication(), SharedPrefUtil.REAL_NAME, string);      //保存真实姓名
                                break;
                            case 105:          //地理位置
                                SharedPrefUtil.setParam(getApplication(), SharedPrefUtil.LOCAL, string);        //保存用户地址
                                break;
                        }
                    } else {
                        Toast.makeText(SetItemActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            }
        });
    }

    protected void init(){
        imageView = findViewById(R.id.complete_back);
        textView = findViewById(R.id.complete_set);
        editText = findViewById(R.id.set_context);
    }
}
