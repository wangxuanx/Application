package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private TextView textView;

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

                System.out.println(groupName);

                if(groupName.equals("")){
                    textView.setText("请输入群组名！！");
                } else {
                    textView.setText("");
                    String url = "https://120.26.172.16:8443/AndroidTest/CreatGroup?groupName="+groupName+"&creatUser="+userName;
                    System.out.println(url);
                    HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                        @Override
                        public void onSuccess(String s) {
                            System.out.println(s);
                            if(s.equals("success register")){        //群组创建成功
                                System.out.println("群组创建成功！！");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText("群组创建成功！！");
                                    }
                                });
                                finish();
                            } else {         //群组名已经存在
                                System.out.println("该群组名已经存在！！");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText("该群组名已经存在！！");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            textView.setText("创建失败！请重试！");
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
    }
}
