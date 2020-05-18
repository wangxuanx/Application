package com.example.application.ui.home.group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.ui.SQL;
import com.example.application.ui.home.sign.Sign;
import com.example.application.ui.home.sign.SignAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryCheckActivity extends AppCompatActivity {
    private ListView listView;
    private List<Sign> signList = new ArrayList<>();
    private SignAdapter signAdapter;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_check);

        setTitle("历史签到");

        title = getIntent().getStringExtra("title");

        init();

        initData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.check_item_name);
                String name = textView.getText().toString();
                Intent intent = new Intent(HistoryCheckActivity.this, AllSignUserActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    private void init() {
        listView = findViewById(R.id.check_list);
    }

    private void initData() {
        signList.clear();

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data", null, 1, SQL.sql_create_sign_list);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.CreateTable(db);

        Cursor cursor = db.query("sign_list", null, "groupName = ?", new String[]{title}, null, null, "id");
        cursor.moveToNext();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            Sign sign = new Sign();

            sign.setType(Integer.parseInt(cursor.getString(2)));
            sign.setTitle(cursor.getString(1));
            long l = cursor.getLong(3);
            sign.setTime(SQL.formatLongToTimeStr(l));
            sign.setUser(cursor.getString(6));

            signList.add(sign);
            cursor.moveToNext();
        }

        signAdapter = new SignAdapter(this, R.layout.check_item, signList);
        listView.setAdapter(signAdapter);
        signAdapter.notifyDataSetChanged();

        cursor.close();
        db.close();
        databaseHelper.close();
    }
}
