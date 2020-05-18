package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMValueCallBack;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private TextView textView;
    private EditText editTextIntroduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group);

        setTitle("");

        init();

        String userName = SharedPrefUtil.getUserName(this);

        button.setOnClickListener(new View.OnClickListener() {          //创建群组
            @Override
            public void onClick(View view) {
                String groupName = editText.getText().toString().trim();        //获取建立的群组名称
                String introduction = editTextIntroduction.getText().toString().trim();       //获取群组介绍

                System.out.println(groupName);

                if(groupName.equals("")){
                    textView.setText("请输入群组名！！");
                } else {
                    textView.setText("");
                    /**
                     * 腾讯IM创建群组
                     * */
                    TIMGroupManager.CreateGroupParam createGroupParam = new TIMGroupManager.CreateGroupParam("Public", groupName);
                    createGroupParam.setIntroduction(introduction);     //设置简介

                    //创建群组
                    TIMGroupManager.getInstance().createGroup(createGroupParam, new TIMValueCallBack<String>() {
                        @Override
                        public void onError(int code, String desc) {
                            Log.d("create Group failed", "create group failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(String s) {
                            Log.d("create Group success", "create group success, groupId:" + s);
                            finish();
                        }
                    });


                }

            }
        });
    }

    private void init(){
        editText = findViewById(R.id.create_group_name);
        button = findViewById(R.id.create_group_button);
        textView = findViewById(R.id.create_group_info);
        editTextIntroduction = findViewById(R.id.create_group_introduction);
    }
}
