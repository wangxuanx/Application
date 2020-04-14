package com.example.application.ui.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.face.utils.Md5;
import com.example.application.http.HttpUtil;
import com.example.application.http.HttpsUtil;
import com.example.application.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText passwordAgain;
    Button registerConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        passwordAgain = findViewById(R.id.register_password_again);
        registerConfirm = findViewById(R.id.register_confirm);

        registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable editable_name = username.getText();
                Editable editable_password = password.getText();
                Editable editable_password_again = passwordAgain.getText();

                if(editable_password.equals(editable_password_again)){

                    String url="https://120.26.172.16:8443/AndroidTest/registUser?registname="+editable_name.toString().trim()+"&password="+ Md5.MD5(editable_password.toString().trim(), "utf-8");
                    HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                        @Override
                        public void onSuccess(String s) {
                            if(s.equals("user already exist!")){
                                Toast.makeText(RegisterActivity.this, "注册用户名已存在，请重新填写！", Toast.LENGTH_SHORT).show();
                            } else if(s.equals("regist successfully!")){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFail(Exception e) {

                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不一致！！！请重新输入！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
