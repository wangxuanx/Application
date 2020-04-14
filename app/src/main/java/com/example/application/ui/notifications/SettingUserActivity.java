package com.example.application.ui.notifications;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.http.SharedPrefUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingUserActivity extends Activity implements View.OnClickListener {

    public static final int SET_HEAD = 101;
    public static final int SET_INFO = 102;
    public static final int SET_NAME = 103;
    public static final int SET_SEX = 104;
    public static final int SET_LOCAL = 105;
    public static final int SET_PASSWORD = 106;

    protected LinearLayout layout_head;
    protected LinearLayout layout_info;
    protected LinearLayout layout_name;
    protected LinearLayout layout_sex;
    protected LinearLayout layout_local;
    protected LinearLayout layout_password;

    protected CircleImageView head_image;
    protected TextView info_text;
    protected TextView name_text;
    protected TextView sex_text;
    protected TextView local_text;

    protected TextView complete_text;
    protected ImageView complete_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);

        layout_head = findViewById(R.id.set_head);
        layout_info = findViewById(R.id.set_info);
        layout_name = findViewById(R.id.set_name);
        layout_sex = findViewById(R.id.set_sex);
        layout_local = findViewById(R.id.set_local);
        layout_password = findViewById(R.id.set_password);

        head_image = findViewById(R.id.set_head_image);
        info_text = findViewById(R.id.set_info_text);
        name_text = findViewById(R.id.set_name_text);
        sex_text = findViewById(R.id.set_sex_text);
        local_text = findViewById(R.id.set_local_text);

        complete_text = findViewById(R.id.complete_set);
        complete_back = findViewById(R.id.complete_back);

        info_text.setText(SharedPrefUtil.getUserName(this));        //设置用户名
        String name = SharedPrefUtil.getRealName(this);
        if(SharedPrefUtil.getRealName(this).equals("null")){
            layout_name.setOnClickListener(this);            //真实姓名未注册
            name_text.setText("未实名");
        } else {
            name_text.setText(SharedPrefUtil.getRealName(this));        //设置真实姓名
        }

        String sex;
        if(SharedPrefUtil.getSex(this) == 1){
            sex = "男";
        } else {
            sex = "女";
        }

        sex_text.setText(sex);          //设置用户性别
        local_text.setText(SharedPrefUtil.getLocal(this));             //设置用户地址

        layout_head.setOnClickListener(this);
        layout_info.setOnClickListener(this);
        layout_sex.setOnClickListener(this);
        layout_local.setOnClickListener(this);
        layout_password.setOnClickListener(this);
        complete_text.setOnClickListener(this);
        complete_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_head:
                break;
            case R.id.set_info:
                Intent intent1 = new Intent(SettingUserActivity.this, SetItemActivity.class);
                intent1.putExtra("type", SET_INFO);
                startActivityForResult(intent1, SET_INFO);
                break;
            case R.id.set_name:
                Intent intent2 = new Intent(SettingUserActivity.this, SetItemActivity.class);
                intent2.putExtra("type", SET_NAME);
                startActivityForResult(intent2, SET_NAME);
                break;
            case R.id.set_sex:
                String title = "选择性别";
                String[] items = new String[]{"男","女"};
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        SharedPrefUtil.setParam(getApplication(), SharedPrefUtil.SEX, 1);           //男
                                        sex_text.setText("男");
                                        break;
                                    case 1:
                                        SharedPrefUtil.setParam(getApplication(), SharedPrefUtil.SEX, 0);           //女
                                        sex_text.setText("女");
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.set_local:
                Intent intent4 = new Intent(SettingUserActivity.this, SetItemActivity.class);
                intent4.putExtra("type", SET_LOCAL);
                startActivityForResult(intent4, SET_LOCAL);
                break;
            case R.id.set_password:
                Intent intent5 = new Intent(SettingUserActivity.this, SetItemActivity.class);
                intent5.putExtra("type", SET_PASSWORD);
                startActivityForResult(intent5, SET_PASSWORD);
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SET_INFO:
                info_text.setText(SharedPrefUtil.getUserName(this));
                break;
            case SET_NAME:
                name_text.setText(SharedPrefUtil.getRealName(this));
                break;
            case SET_LOCAL:
                local_text.setText(SharedPrefUtil.getLocal(this));
                break;
        }
    }

}
