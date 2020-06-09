package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupSystemElemType;

import org.json.JSONArray;
import org.json.JSONObject;

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
        String groupID = getIntent().getStringExtra("groupid");
        setTitle("添加群组");

        init();

        nameText.setText(title);
        String userName = SharedPrefUtil.getUserName(this);        //用户名

        button.setOnClickListener(new View.OnClickListener() {             //点击按钮添加群组
            @Override
            public void onClick(View view) {
                System.out.println(title);

                AddGroup(userName, groupID);       //加入群组

                finish();
            }
        });
    }

    private void init(){
        circleImageView = findViewById(R.id.add_group_head);
        nameText = findViewById(R.id.add_group_name);
        descripeText = findViewById(R.id.add_group_descripe);
        button = findViewById(R.id.add_group_button);
    }

    private void AddGroup(String userName, String group_Id){          //向服务器发送信息添加入本群组

        /*TIMGroupManager.getInstance().applyJoinGroup(group_Id, "some reason", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e("tag", "applyJoinGroup err code = " + i + ", desc = " + s);
            }

            @Override
            public void onSuccess() {
                Log.i("tag", "applyJoinGroup success");
            }
        });*/

        group_Id = group_Id.replace("#", "%23");
        String url = "https://120.26.172.16:8443/AndroidTest/AddUserToGroup?groupid="+group_Id+"&username="+userName;
        System.out.println(url);

        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);        //应答JSON数据

                    String result = jsonObject.getString("ActionStatus");       //获取处理结果
                    if (result.equals("OK")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("MemberList");        //获取加入的列表

                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);       //获取第一个

                        int RESULT = Integer.parseInt(jsonObject1.getString("Result"));       //获取结果

                        if (RESULT == 0) {        //加入失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddGroupActivity.this, "加入群组失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (RESULT == 1) {          //加入成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddGroupActivity.this, "加入群组成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {          //已经为群成员
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddGroupActivity.this, "已经是群组成员！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddGroupActivity.this, "加入群组失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }
}
