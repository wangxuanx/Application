package com.example.application.ui.home.group;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.ui.SQL;
import com.example.application.ui.home.sign.SignUser;
import com.example.application.ui.home.sign.SignUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllSignUserActivity extends AppCompatActivity {
    private ListView listView;
    private List<SignUser> userList = new ArrayList<>();
    private SignUserAdapter signUserAdapter;

    private String title;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sign_user);

        title = getIntent().getStringExtra("title");
        name = getIntent().getStringExtra("name");

        setTitle(name + "签到人员");

        init();

        initData();

    }

    private void init() {
        listView = findViewById(R.id.all_sign_user);
    }

    private void initData() {

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data", null, 1, SQL.getCheckSql(title));
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.CreateTable(db);

        Cursor cursor = db.query(name+ "_check_user_list", null, null, null, null, null, "id");
        cursor.moveToNext();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            SignUser signUser = new SignUser();

            signUser.setUserName(cursor.getString(1));
            signUser.setRealName(cursor.getString(2));

            userList.add(signUser);

            cursor.moveToNext();
        }

        signUserAdapter = new SignUserAdapter(this, R.layout.check_user_item, userList);
        listView.setAdapter(signUserAdapter);
        signUserAdapter.notifyDataSetChanged();

        cursor.close();
        db.close();
        databaseHelper.close();
    }
}
