package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupActivity extends AppCompatActivity {
    /**加入群组*/
    private CircleImageView circleImageView;
    private TextView nameText;
    private TextView descripeText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        String title = getIntent().getStringExtra("groupName");
        setTitle("添加群组");

        init();

        nameText.setText(title);
        String userName = SharedPrefUtil.getUserName(this);        //用户名

        button.setOnClickListener(new View.OnClickListener() {             //点击按钮添加群组
            @Override
            public void onClick(View view) {
                System.out.println(title);

                AddGroup(title, userName);
            }
        });
    }

    private void init(){
        circleImageView = findViewById(R.id.add_group_head);
        nameText = findViewById(R.id.add_group_name);
        descripeText = findViewById(R.id.add_group_descripe);
        button = findViewById(R.id.add_group_button);
    }

    private void AddGroup(String title, String userName){          //向服务器发送信息添加入本群组
        String url = "https://120.26.172.16:8443/AndroidTest/GroupAddUser?groupName="+title+"&addUser="+userName;
        System.out.println(url);

        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                System.out.println(s);
                if(s.equals("add user success")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddGroupActivity.this, "加入群组成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddGroupActivity.this, "加入群组失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
