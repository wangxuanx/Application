package com.example.application.ui.dashboard;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.barnettwong.dragfloatactionbuttonlibrary.view.DragFloatActionButton;
import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.http.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private final int FINISH = 111;
    private final String sql = "CREATE TABLE IF NOT EXISTS leave_list (ID int(20) not null, TITLE varchar(255) not null, TYPE varchar(255) not null, USER varchar(255) not null, BEGINTIME datetime not null, ENDTIME datetime not null, STATE varchar(20) not null, primary key(ID))";


    private DashboardViewModel dashboardViewModel;
    private DragFloatActionButton floatActionButton;

    private ListView listView;
    private List<Check> checkList = new ArrayList<>();

    private CheckAdapter checkAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(root);

        initData();       //初始化数据

        checkAdapter = new CheckAdapter(getContext(), R.layout.leave_item, checkList);
        listView.setAdapter(checkAdapter);

        floatActionButton.setOnClickListener(new View.OnClickListener() {       //点击添加按钮
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LeaveActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        return root;

    }

    private void init(View root){
        floatActionButton = root.findViewById(R.id.circle_button);
        listView = root.findViewById(R.id.leave_list);
    }

    private void initData(){
        /*DatabaseHelper helper = new DatabaseHelper(getContext(), null, null, 1, "create table user(name varchar(20))");
        String sql = "CREATE TABLE IF NOT EXISTS leave_list (ID int(20) not null, TITLE varchar(255) not null, TYPE varchar(255) not null, USER varchar(255) not null, BEGINTIME datetime not null, ENDTIME datetime not null, STATE varchar(20) not null, primary key(ID))";
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "leave_list", null, 1, sql);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", 1);
        values.put("TITLE", "我的请假条");
        values.put("TYPE", "病假");
        values.put("USER", "test");
        values.put("BEGINTIME", "2020-4-1 12");
        values.put("ENDTIME", "2020-4-3 12");
        values.put("STATE", "待审核");
        db.insert("leave_list",null, values);*/

        /**从数据库查找数据*/
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "leave_list", null, 1, sql);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("leave_list", null, null, null, null, null, "ID");
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){
            Check check = new Check();
            check.setId(cursor.getInt(0));
            check.setTitle(cursor.getString(1));
            check.setType(cursor.getString(2));
            check.setUser(cursor.getString(3));
            check.setBeginTime(cursor.getString(4));
            check.setEndTime(cursor.getString(5));
            check.setState(cursor.getString(6));

            System.out.println("数据库得到："+check);

            checkList.add(check);
            cursor.moveToNext();
        }

        Check check = new Check(1,"我的请假条", "事假", "test", "2020-4-1 12", "2020-4-3 12" ,"待审核");
        checkList.add(check);
        Check check1 = new Check(2,"待批准请假条", "病假", "wangxuan", "2020-4-14 12", "2020-4-18 12" ,"待审核");
        checkList.add(check1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:           //从建立请假条开始
                if (resultCode == FINISH){        //返回值为完成后更新数据
                    String type = data.getStringExtra("type");
                    String title = "我的请假条";
                    String beginTime = data.getStringExtra("beginTime");
                    String endTime = data.getStringExtra("endTime");
                    String state = "待审核";
                    Check check = new Check(checkList.size()+1, title, type, SharedPrefUtil.getUserName(getContext()), beginTime, endTime, state);
                    checkList.add(check);
                    checkAdapter.notifyDataSetChanged();

                    /**本地数据库插入数据*/
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "leave_list", null, 1, sql);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("ID", checkList.size()+1);
                    values.put("TITLE", title);
                    values.put("TYPE", type);
                    values.put("USER", SharedPrefUtil.getUserName(getContext()));
                    values.put("BEGINTIME", beginTime);
                    values.put("ENDTIME", endTime);
                    values.put("STATE", state);
                    db.insert("leave_list",null, values);
                }
                break;
        }
    }
}