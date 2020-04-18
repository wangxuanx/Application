package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group);

        setTitle("");

        init();

        String groupName = editText.getText().toString().trim();        //获取建立的群组名称
        String userName = SharedPrefUtil.getUserName(this);

        button.setOnClickListener(new View.OnClickListener() {          //创建群组
            @Override
            public void onClick(View view) {
                String url = "https://120.26.172.16:8443/AndroidTest/CreatGroup?groupName="+groupName+"&creatUser="+userName;
                HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        System.out.println(s);
                        if(s.equals("success register")){        //群组创建成功

                        } else if(s.equals("group already exist!")){         //群组名已经存在
                            Toast.makeText(CreateGroupActivity.this, "该群组已经存在！！！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
            }
        });
    }

    private void init(){
        editText = findViewById(R.id.create_group_name);
        button = findViewById(R.id.create_group_button);
    }
}
