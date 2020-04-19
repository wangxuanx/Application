package com.example.application.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.http.HttpUtil;
import com.example.application.http.HttpsUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends AppCompatActivity {

    protected EditText editText;
    protected Button button;
    protected ListView listView;

    private List<Group> GroupList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        editText = findViewById(R.id.group_edit);
        button = findViewById(R.id.group_button);
        listView = findViewById(R.id.group_list);

        Editable searchText = editText.getText();         //获取搜索的内容

        button.setOnClickListener(new View.OnClickListener() {                //点击搜索按钮
            @Override
            public void onClick(View view) {
                GroupList = new ArrayList<>();

                if(searchText.toString().trim().equals("")){
                    Toast.makeText(SearchGroupActivity.this, "搜索内容不能为空！", Toast.LENGTH_SHORT).show();
                } else {

                    initList(searchText.toString().trim());

                    GroupAdapter adapter = new GroupAdapter(SearchGroupActivity.this, R.layout.group_item, GroupList);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Group group = GroupList.get(i);
                            Toast.makeText(SearchGroupActivity.this, group.getGroupName(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SearchGroupActivity.this, AddGroupActivity.class);
                            intent.putExtra("groupName", group.getGroupName());
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void initList(String search){             //调用api来获取搜索到到数据
        String path = "https://120.26.172.16:8443/AndroidTest/SearchGroup?group_search="+search;       //098F6BCD4621D373CADE4E832627B4F6
        try {

            HttpsUtil.getInstance().get(path, new HttpsUtil.OnRequestCallBack() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("搜索群组: "+s);
                    Gson gson =new Gson();
                    GroupBean groupBean = gson.fromJson(s, GroupBean.class);        //解析json数据
                    String list = groupBean.getGroup_list();             //获取到搜索到到列表
                    System.out.println(list);

                    String[] all = list.split(",");        //将字符分割开
                    for(int i = 0; i<all.length; i++){
                        System.out.println("第"+(i+1)+"个："+all[i]);
                        Group group = new Group(R.drawable.default_head, all[i].trim(), "群组描述");
                        GroupList.add(group);
                    }
                }

                @Override
                public void onFail(Exception e) {
                    Log.e("错误", "搜索错误");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
