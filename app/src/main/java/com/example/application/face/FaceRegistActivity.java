package com.example.application.face;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.face.utils.ImageSaveUtil;
import com.example.application.face.utils.Md5;
import com.example.application.http.SharedPrefUtil;

import java.io.File;

public class FaceRegistActivity extends Activity {
    private Button button;
    private ImageView imageView;
    private TextView textView;

    private static final int FACE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_regist);
        findView();
        if(SharedPrefUtil.getFaceState(this)){
            imageView.setImageResource(R.drawable.face_ok);
            textView.setText("恭喜，人脸已完成注册！");
            button.setText("注册完成");
            button.setEnabled(false);
        }



        button.setOnClickListener(new View.OnClickListener() {        //点击注册
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FaceRegistActivity.this, FaceDetectActivity.class);            //启动注册人脸进程
                startActivityForResult(intent, FACE);

            }
        });
    }

    private void findView() {
        imageView = findViewById(R.id.image_check);
        textView = findViewById(R.id.text_check);
        button = findViewById(R.id.register_check);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FACE:             //从人脸返回
                String filePath = ImageSaveUtil.loadCameraBitmapPath(this, "head_tmp.jpg");
                Bitmap bitmap = ImageSaveUtil.loadCameraBitmap(this, "head_tmp.jpg");
                if(!filePath.equals("")){
                    final File file = new File(filePath);
                    System.out.println("输出文件");
                    System.out.println(file);
                    APIService.getInstance().RegFace(file, Md5.MD5(SharedPrefUtil.getUserName(this), "utf-8"));         //调用api进行人脸注册操作

                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            //execute the task
                            imageView.setImageResource(R.drawable.face_ok);
                            textView.setText("恭喜，人脸已完成注册！");
                            button.setText("注册完成");
                            button.setEnabled(false);
                        }
                    }, 0);
                }


                break;
        }
    }
}
