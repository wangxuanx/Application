package com.example.application.ui.home.group;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.face.APIService;
import com.example.application.face.FaceDetectActivity;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.face.utils.ImageSaveUtil;
import com.example.application.face.utils.Md5;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.SQL;
import com.example.application.ui.home.sign.SignUser;
import com.example.application.ui.home.sign.SignUserAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainSignActivity extends AppCompatActivity {

    /***
     * 对发起人显示已经签到对人
     * 对其他用户显示签到的入口
     */

    private TextView signName;
    private TextView signUser;
    private TextView signState;
    private TextView userTitle;
    private Button button;

    private ListView listView;
    private SignUserAdapter signUserAdapter;
    private List<SignUser> userList = new ArrayList<>();

    private final int FACE = 100;
    private final int HANDS = 101;
    private int type;

    private String title;
    private String sign_title;
    private String sign_create_user;
    private String sign_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign);

        init();          //初始化控件

        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        sign_title = getIntent().getStringExtra("name");
        sign_create_user = getIntent().getStringExtra("user");
        sign_password = getIntent().getStringExtra("password");

        initCheckUser();        //获取签到的人

        if (type == FACE){
            setTitle("人脸签到");
        } else if (type == HANDS){
            setTitle("手势签到");
        } else {
            setTitle("出现错误！！！");
        }

        signName.setText(sign_title);
        signUser.setText(sign_create_user);

        if (sign_create_user.equals(SharedPrefUtil.getUserName(this))) {          //如果是本人 则不显示签到按钮
            signState.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        } else {
            userTitle.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {             //点击前往签到界面
            @Override
            public void onClick(View view) {
                if (type == FACE){
                    Intent intent = new Intent(MainSignActivity.this, FaceDetectActivity.class);
                    startActivityForResult(intent, FACE);
                } else {
                    Intent intent = new Intent(MainSignActivity.this, HandsSignActivity.class);
                    intent.putExtra("password", sign_password);         //发送密码
                    startActivityForResult(intent, HANDS);
                }
            }
        });
    }

    private void init(){
        signName = findViewById(R.id.sign_name);
        signUser = findViewById(R.id.sign_user);
        signState = findViewById(R.id.sign_state);
        userTitle = findViewById(R.id.user_title);

        button = findViewById(R.id.sign_button);

        listView = findViewById(R.id.sign_user_list);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FACE:          //人脸返回
                if (resultCode == RESULT_OK){
                    String filePath = ImageSaveUtil.loadCameraBitmapPath(this, "head_tmp.jpg");
                    if(!filePath.equals("")){
                        final File file = new File(filePath);
                        System.out.println("输出文件");
                        System.out.println(file);
                        String result = APIService.getInstance().SearchFace(file, Md5.MD5(SharedPrefUtil.getUserName(this), "utf-8"));         //调用api进行人脸搜索操作
                        System.out.println("签到返回:"+result);

                        try {
                            JSONObject object = new JSONObject(result);

                            int error_code = object.getInt("error_code");        //获取错码
                            if (error_code == 0){
                                JSONArray object1 = object.getJSONObject("result").getJSONArray("user_list");

                                JSONObject jsonObject = (JSONObject) object1.get(0);
                                long score = jsonObject.getLong("score");

                                if (score >= 80){          //分数高于80 通过人脸识别

                                    String back = UpData();         //向数据库中修改数据
                                    if (back.equals("success")){
                                        signState.setText("已签到");
                                        button.setEnabled(false);
                                        button.setText("签到完成");
                                    } else {
                                        Toast.makeText(this, "签到失败！请重试！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                break;
            case HANDS:         //手势返回
                /**
                 * 向表中插入数据
                 * */
                if (resultCode == RESULT_OK){
                    String back = UpData();         //向数据库中修改数据
                    if (back.equals("success")){
                        signState.setText("已签到");
                        button.setEnabled(false);
                        button.setText("签到完成");
                    } else {
                        Toast.makeText(this, "签到失败！请重试！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private String UpData(){
        final String[] back = new String[1];
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data", null, 1, com.example.application.ui.SQL.sql_create_sign_list);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.CreateTable(db);
        ContentValues values = new ContentValues();
        values.put("state", 1);         //更新最新消息
        db.update("sign_list", values, "title = ? and type = ? and createUser = ?", new String[]{sign_title, String.valueOf(type), sign_create_user});

        String url = "https://120.26.172.16:8443/AndroidTest/UserSign?user=+"+SharedPrefUtil.getUserName(getApplicationContext())+"&title="+sign_title;

        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                back[0] = s;
            }

            @Override
            public void onFail(Exception e) {

            }
        });

        db.close();
        databaseHelper.close();

        return back[0];
    }

    private void initCheckUser() {           //从本地数据库获取已经签到的人

        userList.clear();

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data", null, 1, SQL.getCheckSql(title));
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.CreateTable(db);

        Cursor cursor = db.query(title+ "_check_user_list", null, null, null, null, null, "id");
        if (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            SignUser signUser = new SignUser();

            signUser.setUserName(cursor.getString(1));
            signUser.setRealName(cursor.getString(2));

            userList.add(signUser);
        }

        signUserAdapter = new SignUserAdapter(this, R.layout.group_item, userList);
        listView.setAdapter(signUserAdapter);
        signUserAdapter.notifyDataSetChanged();

        cursor.close();
        db.close();
        databaseHelper.close();
    }

}
