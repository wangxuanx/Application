package com.example.application.ui.home;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.http.HttpsUtil;
import com.example.application.ui.SQL;
import com.example.application.ui.home.group.Group;
import com.example.application.ui.home.group.GroupActivity;
import com.example.application.ui.home.group.GroupAdapter;
import com.example.application.ui.home.scan.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView newTextView;         //显示新消息红点

    private List<Group> GroupList = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private String AddGroupID;
    private String AddGroupUser;
    private String List = "";
    private List<String> groupList = new ArrayList<>();

    public static final int SEARCH = 101;

    private static ActivityManager activityManager;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.home_list);
        swipeRefreshLayout = root.findViewById(R.id.swipe_list);
        newTextView = root.findViewById(R.id.new_message_view);

        setHasOptionsMenu(true);             /**添加右上角menu*/
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        initList();          //显示已加入的群组


        groupAdapter = new GroupAdapter(getContext(), R.layout.group_item, GroupList);
        listView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {       //点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group group = GroupList.get(i);
                //Toast.makeText(getContext(), group.getGroupName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra("name", group.getGroupName());          //发送群组名称
                intent.putExtra("groupid", group.getGroupID());          //发送群组id
                startActivity(intent);

                view.findViewById(R.id.new_message_view).setVisibility(View.GONE);
                TextView textView = view.findViewById(R.id.group_describe);
                textView.setTextColor(Color.rgb(67,67,67));

                Toast.makeText(getContext(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {            //下拉刷新列表
            @Override
            public void onRefresh() {

                GroupList.clear();

                initList();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(GroupList.size() != 0){

                            groupAdapter = new GroupAdapter(getContext(), R.layout.group_item, GroupList);
                            listView.setAdapter(groupAdapter);
                            groupAdapter.notifyDataSetChanged();
                        }
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getUserAddGroup();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.scan_group:           //扫一扫
                /**以下是启动我们自定义的扫描活动*/
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);      /**通过Fragment启动扫描*/
                intentIntegrator.setPrompt("请对准二维码");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                /**设置启动我们自定义的扫描活动，若不设置，将启动默认活动*/
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
                break;
            case R.id.search_group:           //搜索群组
                Intent intent = new Intent(getActivity(), SearchGroupActivity.class);
                startActivityForResult(intent, SEARCH);
                break;
            case R.id.face_to_face:            //建群
                Intent intent1 = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initList() {             //调用api来获取已经加入的群组

        System.out.println("获取数据了！！！");

        /**
         * 获取加入的群组列表
         */
        //创建回调
        TIMValueCallBack<List<TIMGroupBaseInfo>> cb = new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.e("tag", "get group list failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {//参数返回各群组基本信息
                Log.d("tag", "get group list success");

                for(TIMGroupBaseInfo info : timGroupInfos) {

                    DatabaseHelper databaseHelper1 = new DatabaseHelper(getContext(), "group_list", null, 1, SQL.sql_create_group_list);       //向数据库插入数据
                    SQLiteDatabase db = databaseHelper1.getWritableDatabase();

                    Cursor cursor = db.query("group_list", null, "group_id = ?", new String[]{info.getGroupId()}, null, null, "id");
                    if (cursor.getCount() == 0) {
                        ContentValues values = new ContentValues();
                        values.put("group_id", info.getGroupId());
                        values.put("group_name", info.getGroupName());

                        db.insert("group_list", null, values);
                    }

                    //获取会话扩展实例
                    TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, info.getGroupId());

                    //获取此会话的消息
                    con.getLocalMessage(1, //获取此会话最近的 10 条消息
                            null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                            new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                                @Override
                                public void onError(int code, String desc) {//获取消息失败
                                    //接口返回了错误码 code 和错误描述 desc，可用于定位请求失败原因
                                    //错误码 code 含义请参见错误码表
                                    Log.d("tag", "get message failed. code: " + code + " errmsg: " + desc);
                                }

                                @Override
                                public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                                    //遍历取得的消息
                                    for(TIMMessage msg : msgs) {

                                        for(int i = 0; i < msg.getElementCount(); ++i) {
                                            TIMElem elem = msg.getElement(i);

                                            //获取当前元素的类型
                                            TIMElemType elemType = elem.getType();
                                            Log.d("tag", "elem type: " + elemType.name());
                                            if (elemType == TIMElemType.Text) {
                                                TIMTextElem textElem = (TIMTextElem) elem;

                                                ContentValues values1 = new ContentValues();
                                                values1.put("last_message", textElem.getText());         //更新最新消息
                                                db.update("group_list", values1, "group_id = ?", new String[]{info.getGroupId()});
                                            }
                                        }

                                        //可以通过 timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                                        Log.e("tag", "get msg: " + msg.timestamp() + " self: " + msg.isSelf() + " seq: " + msg.getSeq());


                                    }
                                }
                            });

                    Log.d("tag", "group id: " + info.getGroupId() +
                            " group name: " + info.getGroupName() +
                            " group type: " + info.getGroupType());
                }
            }
        };

        //获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);

        /**从数据库查找数据*/
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "group_list", null, 1, SQL.sql_create_group_list);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("group_list", null, null, null, null, null, "group_id");
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){

            Group group1 = new Group();
            group1.setImageID(R.drawable.default_head);
            group1.setGroupID(cursor.getString(1));
            group1.setGroupName(cursor.getString(2));
            group1.setGroupDescribe(cursor.getString(3));

            groupList.add(cursor.getString(1));

            List = List + cursor.getString(2) + "%23%23";

            GroupList.add(group1);
            cursor.moveToNext();
        }

        initData();

        /*String user = SharedPrefUtil.getUserName(getContext());
        System.out.println("用户" + user);
        String path = "https://120.26.172.16:8443/AndroidTest/GetUserGroup?user=" + user;       //098F6BCD4621D373CADE4E832627B4F6
        try {
            HttpsUtil.getInstance().get(path, new HttpsUtil.OnRequestCallBack() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("群组: " + s);
                    s = s.trim();
                    //Gson gson = new Gson();
                    //GroupBean groupBean = gson.fromJson(s, GroupBean.class);        //解析json数据
                    //String list = groupBean.getGroup_list();             //获取到搜索到到列表
                    if(!s.equals("")){
                        String[] all = s.split(",");        //将字符分割开

                        for (int i = 0; i < all.length; i++) {
                            System.out.println("第" + (i + 1) + "个：" + all[i].trim());
                            Group group = new Group(R.drawable.default_head, all[i].trim(), "test");
                            GroupList.add(group);
                        }
                    }


                }

                @Override
                public void onFail(Exception e) {
                    Log.e("error", "初始化错误!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "扫描失败！！", Toast.LENGTH_LONG).show();
            } else {
                String group_info = result.getContents();         //扫描得到到内容
                String[] info = group_info.split(",");         //分割开群名称与群id
                Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                intent.putExtra("groupName", info[0]);
                intent.putExtra("groupid", info[1]);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getUserAddGroup(){           //获取用户加群信息和新消息

        Thread new_group = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){

                        /**接收申请加群消息*/
                        /**接收新消息*/
                        //设置消息监听器，收到新消息时，通过此监听器回调
                        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
                            @Override
                            public boolean onNewMessages(List<TIMMessage> list) {          //收到新消息
                                //newTextView.setVisibility(View.VISIBLE);
                                Log.d("info", "获取新消息");

                                for(int i = list.size() - 1; i >= 0; --i){
                                    TIMMessage message = list.get(i);
                                    TIMConversation conversation = message.getConversation();
                                    String groupId = conversation.getPeer();          //获取群组ID
                                    System.out.println("群组🆔"+groupId);

                                    for(int j = 0; j< message.getElementCount(); ++j) {
                                        TIMElem elem = message.getElement(i);

                                        //获取当前元素的类型
                                        TIMElemType elemType = elem.getType();
                                        Log.d("tag", "elem type: " + elemType.name());
                                        if (elemType == TIMElemType.Text) {
                                            TIMTextElem textElem = (TIMTextElem)elem;
                                            System.out.println(textElem.getText()+" "+listView.getCount());         //消息内容

                                            freshList(groupId, textElem.getText());       //刷新显示新消息

                                        } else if (elemType == TIMElemType.GroupSystem) {         //群组消息
                                            TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem;
                                            System.out.println("加群消息："+systemElem.getSubtype()+" "+systemElem.getGroupId()+" "+systemElem.getOpUser()+" "+systemElem.getOpReason());

                                            AddGroupID = systemElem.getGroupId().replace("#", "%23");         //获取群组ID
                                            AddGroupUser = systemElem.getOpUser();         //获取申请加群人
                                            String url = "https://120.26.172.16:8443/AndroidTest/AddUserToGroup?username="+AddGroupUser+"&groupid="+AddGroupID;
                                            System.out.println(url);
                                            HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    System.out.println(s);
                                                    Log.d("log", "加入群组成功");

                                                    initList();          //刷新列表
                                                }

                                                @Override
                                                public void onFail(Exception e) {
                                                    Log.d("log", "加入群组失败");
                                                }
                                            });
                                        }
                                    }
                                }
                                return false;     //返回true将终止回调链，不再调用下一个新消息监听器
                            }
                        });

                        Thread.sleep(5000);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        new_group.start();
    }

    private void freshList(String groupId, String content) {
        int position = 0;
        for (int i = 0; i < groupList.size(); i++){
            if (groupList.get(i).equals(groupId)){
                position = i;
            }
        }

        View view = listView.getChildAt(position);
        TextView textView = view.findViewById(R.id.group_describe);
        TextView textView1 = view.findViewById(R.id.new_message_view);
        textView1.setVisibility(View.VISIBLE);
        textView.setText(content);
        textView.setTextColor(Color.RED);
    }

    private void initData(){           //初始化签到数据
        String list = "";

        if (List.length() != 0){
             list = List.substring(0, List.length() - 6);
        }

        String finalList = list;

        Thread new_check = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (true){
                        System.out.println("获取签到数据");

                        String url = "https://120.26.172.16:8443/AndroidTest/GetSign?grouplist="+ finalList;
                        //System.out.println(url);
                        HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                            @Override
                            public void onSuccess(String s) {
                                Log.i("log", "获取签到数据成功");
                                //System.out.println(s);
                                /**创建签到的数据库*/
                                DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "app_data", null, 1, SQL.sql_create_sign_list);
                                databaseHelper.CreateTable();
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    int size = jsonObject.getInt("total");        //获取总共的签到条数

                                    for (int i = 1; i <= size; i++){
                                        JSONObject object = jsonObject.getJSONObject(String.valueOf(i));          //获取签到的object
                                        System.out.println(object.toString());
                                        String title = object.getString("signName");
                                        String type = object.getString("signType");
                                        String dead_time = object.getString("dead_time").substring(0, object.getString("dead_time").length() - 2);
                                        String password = object.getString("password");
                                        String groupName = object.getString("signGroup");
                                        String createUser = object.getString("signCreatUser");

                                        System.out.println(dead_time);
                                        long deadline_time = SQL.DataToLang(dead_time);           //将时间转化为long格式

                                        Cursor cursor = db.query("sign_list", null, "deadline_time = ?", new String[]{String.valueOf(deadline_time)}, null, null, "id");
                                        if (cursor.getCount() == 0) {
                                            ContentValues values = new ContentValues();
                                            values.put("title", title);
                                            values.put("type", type);
                                            values.put("deadline_time", deadline_time);
                                            values.put("password", password);
                                            values.put("groupName", groupName);
                                            values.put("createUser", createUser);
                                            values.put("state", 0);

                                            db.insert("sign_list", null, values);

                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                Log.i("log", "获取签到失败");
                            }
                        });

                        Thread.sleep(5000);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    List = "";
                }

            }
        });

        new_check.start();
    }

}