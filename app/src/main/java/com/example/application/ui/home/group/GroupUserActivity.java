package com.example.application.ui.home.group;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

public class GroupUserActivity extends AppCompatActivity {
    private String Title;
    private TextView textView;
    private ListView listView;
    private String result;
    private String groupId;

    private List<User> userList = new ArrayList<>();

    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_user);

        setTitle("");

        init();

        Title = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupid");

        System.out.println(Title+" "+groupId);

        getConnection(Title);

        textView.setText(Title);
    }

    private void init(){
        textView = findViewById(R.id.group_info_name);
        listView = findViewById(R.id.user_list);
    }

    private void getConnection(String grahame){

        //创建回调
        TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息

                for(TIMGroupMemberInfo info : infoList) {
                    Log.d("tag", "user: " + info.getUser() +
                            "join time: " + info.getJoinTime() +
                            "role: " + info.getRole());

                    User user1 = new User(info.getUser(), R.drawable.default_head, "1");
                    userList.add(user1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userAdapter = new UserAdapter(getApplicationContext(), R.layout.user_item, userList);
                            listView.setAdapter(userAdapter);
                        }
                    });
                }
            }
        };

        //获取群组成员信息
        TIMGroupManager.getInstance().getGroupMembers(
                groupId, //群组 ID
                cb);     //回调

        /*String url = "https://120.26.172.16:8443/AndroidTest/GetUserList?groupName="+grahame;
        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
            @Override
            public void onSuccess(String s) {
                setResult(s);
                Gson gson =new Gson();
                GroupBean groupBean = gson.fromJson(s, GroupBean.class);

                String groupList = groupBean.getUserName();
                String realList = groupBean.getRealName();

                String[] group = groupList.split(",");
                String[] real = realList.split(",");

                for(int i = 0; i < group.length; i++){
                    System.out.println(group[i]+"和"+real[i]);
                    User user1 = new User(group[i], R.drawable.default_head, real[i]);
                    userList.add(user1);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userAdapter = new UserAdapter(getApplicationContext(), R.layout.user_item, userList);
                        listView.setAdapter(userAdapter);
                    }
                });


                System.out.println(s+" "+groupList+" "+userList);
            }

            @Override
            public void onFail(Exception e) {

            }
        });*/

    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
