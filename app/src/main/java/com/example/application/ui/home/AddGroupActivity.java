package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupActivity extends AppCompatActivity {
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

        button.setOnClickListener(new View.OnClickListener() {             //点击按钮添加群组
            @Override
            public void onClick(View view) {
                AddGroup(title);
            }
        });
    }

    private void init(){
        circleImageView = findViewById(R.id.add_group_head);
        nameText = findViewById(R.id.add_group_name);
        descripeText = findViewById(R.id.add_group_descripe);
        button = findViewById(R.id.add_group_button);
    }

    private void AddGroup(String title){          //向服务器发送信息添加入本群组
        String url = "https://"+title;
    }
}
