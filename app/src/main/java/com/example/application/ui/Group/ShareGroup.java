package com.example.application.ui.Group;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.application.R;

public class ShareGroup extends AppCompatActivity {
    /**生成二维码并显示*/
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group);

        setTitle("群组二维码");

        imageView = findViewById(R.id.erweima);

        String title = getIntent().getStringExtra("groupName");

        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(title, 580, 580);

        imageView.setImageBitmap(mBitmap);
    }
}
