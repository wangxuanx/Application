package com.example.application.ui.dashboard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.barnettwong.dragfloatactionbuttonlibrary.view.DragFloatActionButton;
import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.SQL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private final int FINISH = 111;

    private DragFloatActionButton floatActionButton;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private List<Check> checkList = new ArrayList<>();

    private CheckAdapter checkAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initDataFromServer();       //从服务器获取
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(root);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        initData();       //初始化数据

        initDataFromServer();       //从服务器获取

        checkAdapter = new CheckAdapter(getContext(), R.layout.leave_item, checkList);
        listView.setAdapter(checkAdapter);

        floatActionButton.setOnClickListener(new View.OnClickListener() {       //点击添加按钮
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LeaveActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = view.findViewById(R.id.check_title);
                TextView textView1 = view.findViewById(R.id.check_state);
                TextView textView2 = view.findViewById(R.id.check_user);
                TextView textView3 = view.findViewById(R.id.beginTime);
                TextView textView4 = view.findViewById(R.id.endTime);

                String checkTitle = textView.getText().toString();
                String user = textView2.getText().toString();     //获取申请人
                String beginTime = textView3.getText().toString();      //起始时间
                String endTime = textView4.getText().toString();         //终止时间
                if (checkTitle.equals("待批准请假条")){
                    String title = "选择操作";
                    String[] items = new String[]{"通过审核", "未通过"};
                    new AlertDialog.Builder(getContext())
                            .setTitle(title)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                    switch (which) {
                                        case 0:
                                            String back1 = Leave(user, beginTime, endTime, true);
                                            System.out.println(back1);
                                            if (back1.equals("success")){
                                                textView1.setText("通过审核");
                                                textView1.setBackgroundColor(getResources().getColor(R.color.pass));
                                            }
                                            break;
                                        case 1:
                                            String back2 = Leave(user, beginTime, endTime, false);
                                            if (back2.equals("success")){
                                                textView1.setText("未通过");
                                                textView1.setBackgroundColor(getResources().getColor(R.color.refuse));
                                            }
                                            break;
                                    }
                                }
                            }).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkList.clear();        //清空签到

                initData();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(checkList.size() != 0){

                            checkAdapter = new CheckAdapter(getContext(), R.layout.leave_item, checkList);
                            listView.setAdapter(checkAdapter);
                            checkAdapter.notifyDataSetChanged();
                        }
                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;

    }

    private void init(View root){
        floatActionButton = root.findViewById(R.id.circle_button);
        swipeRefreshLayout = root.findViewById(R.id.leave_swipe_list);
        listView = root.findViewById(R.id.leave_list);
    }

    private void initData(){

        /**从数据库查找数据*/
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "app_data", null, 1, SQL.sql_create_leave_list);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.CreateTable(db);
        Cursor cursor = db.query("leave_list", null, null, null, null, null, "id");
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){
            Check check = new Check();
            check.setId(cursor.getInt(0));
            check.setTitle(cursor.getString(1));
            check.setType(cursor.getString(2));
            check.setUser(cursor.getString(3));
            check.setBeginTime(cursor.getString(4));
            check.setEndTime(cursor.getString(5));
            check.setState(cursor.getString(7));

            System.out.println("数据库得到："+check);

            checkList.add(check);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        databaseHelper.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:           //从建立请假条开始
                if (resultCode == FINISH){        //返回值为完成后更新数据
                    String type = data.getStringExtra("type");
                    String title = "我的请假条";
                    String otherInfo = data.getStringExtra("otherInfo");
                    String beginTime = data.getStringExtra("beginTime");
                    String endTime = data.getStringExtra("endTime");
                    String state = "待审核";
                    Check check = new Check(checkList.size()+1, title, type, otherInfo, SharedPrefUtil.getUserName(getContext()), beginTime, endTime, state);
                    checkList.add(check);
                    checkAdapter.notifyDataSetChanged();

                    /**本地数据库插入数据*/
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "app_data", null, 1, SQL.sql_create_leave_list);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("type", type);
                    values.put("user", SharedPrefUtil.getUserName(getContext()));
                    values.put("beginTime", beginTime);
                    values.put("endTime", endTime);
                    values.put("otherInfo", otherInfo);
                    values.put("state", state);
                    db.insert("leave_list",null, values);

                    db.close();
                    databaseHelper.close();
                }
                break;
        }
    }

    private void initDataFromServer(){            //从服务器获取请假条

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (true) {
                    String url = "https://120.26.172.16:8443/AndroidTest/GetLeave?user="+SharedPrefUtil.getUserName(getContext());

                    HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                        @Override
                        public void onSuccess(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String title = null;
                                System.out.println(jsonObject);

                                int total = jsonObject.getInt("total");          //总数

                                for (int i = 1; i <= total; i++) {
                                    JSONObject object = jsonObject.getJSONObject(String.valueOf(i));

                                    String type = object.getString("type");
                                    String beginTime = object.getString("beginTime");
                                    String endTime = object.getString("endTime");
                                    String otherInfo = object.getString("otherInfo");
                                    String state = object.getString("state");
                                    String belongUser = object.getString("belongUser");
                                    String checkUser = object.getString("checkUser");

                                    if (belongUser.equals(SharedPrefUtil.getUserName(getContext()))) {
                                        title = "我的请假条";
                                    } else if (checkUser.equals(SharedPrefUtil.getUserName(getContext()))) {
                                        title = "待批准请假条";
                                    }

                                    /**本地数据库插入数据*/
                                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "app_data", null, 1, SQL.sql_create_leave_list);
                                    SQLiteDatabase db = databaseHelper.getWritableDatabase();

                                    Cursor cursor = db.query("leave_list", null, "type = ? AND beginTime = ? AND endTime = ?", new String[]{type, beginTime, endTime}, null, null, "id");
                                    if (cursor.getCount() == 0) {           //无数据，插入数据
                                        ContentValues values = new ContentValues();
                                        values.put("title", title);
                                        values.put("type", type);
                                        values.put("user", belongUser);
                                        values.put("beginTime", beginTime);
                                        values.put("endTime", endTime);
                                        values.put("otherInfo", otherInfo);
                                        values.put("state", state);
                                        db.insert("leave_list",null, values);
                                    } else {         //有数据，更新其中数据与服务器同步
                                        ContentValues values = new ContentValues();
                                        values.put("state", state);

                                        if (title.equals("待批准请假条")) {
                                            db.update("leave_list",values, "title = ? AND user = ? AND beginTime = ? AND endTime = ?", new String[]{"待批准请假条", belongUser, beginTime, endTime});
                                        } else {
                                            db.update("leave_list",values, "title = ? AND user = ? AND beginTime = ? AND endTime = ?", new String[]{"我的请假条", belongUser, beginTime, endTime});
                                        }
                                    }
                                    cursor.close();
                                    db.close();
                                    databaseHelper.close();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(Exception e) {

                        }
                    });

                    Thread.sleep(10000);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private String Leave(String user, String beginTime, String endTime, boolean state){           //通过请假条

        final String[] back = new String[1];
        String url = "https://120.26.172.16:8443/AndroidTest/DoLeave?belongUser="+user+"&checkUser="+SharedPrefUtil.getUserName(getContext())+"&beginTime="+beginTime+"&endTime="+endTime+"&state="+state;
        String stateString;

        System.out.println(url);

        if (state == true) {
            stateString = "通过审核";
        } else {
            stateString = "未通过";
        }

        try {

            HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                @Override
                public void onSuccess(String s) {
                    back[0] = s;
                }

                @Override
                public void onFail(Exception e) {

                }
            });

            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (back[0].equals("success")){         //成功就插入信息
            /**本地数据库插入数据*/
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "app_data", null, 1, SQL.sql_create_leave_list);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            databaseHelper.CreateTable(db);

            ContentValues values = new ContentValues();
            values.put("state", stateString);
            db.update("leave_list",values, "title = ? AND user = ? AND beginTime = ? AND endTime = ?", new String[]{"待批准请假条", user, beginTime, endTime});

            db.close();
            databaseHelper.close();
        }

        return back[0];
    }

}