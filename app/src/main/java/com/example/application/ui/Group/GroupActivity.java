package com.example.application.ui.Group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.ui.msg.Msg;
import com.example.application.ui.msg.MsgAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private ImageView imageView;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private EditText editText;
    private Button button;

    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter msgAdapter;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        title = getIntent().getStringExtra("name");         //接收群组名称
        setTitle(title);

        init();

        initMsg();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);

        button.setOnClickListener(new View.OnClickListener() {           //点击发送消息
            @Override
            public void onClick(View view) {
                String string = editText.getText().toString().trim();          //获取需要发送的消息
                if(!"".equals(string)){
                    Msg msg = new Msg("user", string, Msg.SEND);
                    msgList.add(msg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);          //有新消息，刷新显示
                    recyclerView.scrollToPosition(msgList.size() - 1);              //将view定位到最后一行
                    editText.setText("");             //清空输入
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {         //设置显示更多功能
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {             //点击空白区域关闭键盘
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //关闭键盘
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                relativeLayout.setVisibility(View.GONE);

                button.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {              //点击文本框
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                relativeLayout.setVisibility(View.GONE);

                button.setVisibility(View.VISIBLE);         //显示发送按钮
                imageView.setVisibility(View.GONE);          //隐藏多功能按钮
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {              //添加menu菜单
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_group:
                Intent intent = new Intent(GroupActivity.this, ShareGroup.class);
                intent.putExtra("groupName", title);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void init(){
        imageView = findViewById(R.id.ivAdd);
        relativeLayout = findViewById(R.id.bottom_layout);
        recyclerView = findViewById(R.id.rv_chat_list);
        editText = findViewById(R.id.et_content);
        button = findViewById(R.id.rv_sent_button);
    }

    private void initMsg(){
        Msg msg = new Msg("user1", "第一条信息", Msg.RECEIVE);
        msgList.add(msg);
        Msg msg1 = new Msg("user2", "第二条信息", Msg.SEND);
        msgList.add(msg1);
    }
}
