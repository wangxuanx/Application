package com.example.application.ui.home.group;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.face.APIService;
import com.example.application.face.FaceDetectActivity;
import com.example.application.face.utils.ImageSaveUtil;
import com.example.application.face.utils.Md5;
import com.example.application.http.SharedPrefUtil;

import java.io.File;

public class MainSignActivity extends AppCompatActivity {

    /***
     * 对发起人显示已经签到对人
     * 对其他用户显示签到的入口
     */

    private TextView signName;
    private TextView signType;
    private TextView signState;

    private Button button;

    private ListView listView;

    private final int FACE = 100;
    private final int HANDS = 101;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign);

        init();          //初始化控件

        type = getIntent().getIntExtra("type", 0);
        if (type == FACE){
            setTitle("人脸签到");
        } else if (type == HANDS){
            setTitle("手势签到");
        } else {
            setTitle("出现错误！！！");
        }

        button.setOnClickListener(new View.OnClickListener() {             //点击前往签到界面
            @Override
            public void onClick(View view) {
                if (type == FACE){
                    Intent intent = new Intent(MainSignActivity.this, FaceDetectActivity.class);
                    startActivityForResult(intent, FACE);
                } else {

                }
            }
        });
    }

    private void init(){
        signName = findViewById(R.id.sign_name);
        signType = findViewById(R.id.sign_type);
        signState = findViewById(R.id.sign_state);

        button = findViewById(R.id.sign_button);

        listView = findViewById(R.id.sign_user_list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FACE:          //人脸返回
                String filePath = ImageSaveUtil.loadCameraBitmapPath(this, "head_tmp.jpg");
                if(!filePath.equals("")){
                    final File file = new File(filePath);
                    System.out.println("输出文件");
                    System.out.println(file);
                    APIService.getInstance().SearchFace(file, Md5.MD5(SharedPrefUtil.getUserName(this), "utf-8"));         //调用api进行人脸搜索操作
                }
                break;
            case HANDS:         //手势返回
                /**
                 * 向表中插入数据
                 * */
                break;
        }
    }
}
