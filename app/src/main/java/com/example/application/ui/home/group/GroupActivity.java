package com.example.application.ui.home.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.face.utils.DatabaseHelper;
import com.example.application.face.utils.LogUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.SQL;
import com.example.application.ui.dashboard.Check;
import com.example.application.ui.home.SignInActivity;
import com.example.application.ui.home.msg.Msg;
import com.example.application.ui.home.msg.MsgAdapter;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private ImageView imageView;
    private RecyclerView recyclerView;
    private EditText editText;
    private Button button;

    private RelativeLayout relativeLayout;
    private RelativeLayout faceRelativeLayout;
    private RelativeLayout handsRelativeLayout;
    private LinearLayout signLayout;

    private String title;
    private String groupID;
    private int type;

    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter msgAdapter;

    private TIMConversation conversation;

    private TextView signName;
    private TextView liftTime;
    private long leftTime;     //剩余时间

    private final int FACE = 100;
    private final int HANDS = 101;
    private final int SHARE = 110;
    private final int INFO = 111;

    private String SQL;          //数据库sql语句

    private String Sign_Title;            //签到名称
    private String Sign_Create_User;         //签到创建人
    private String Sign_Password;          //手势签到密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        title = getIntent().getStringExtra("name");         //接收群组名称
        groupID = getIntent().getStringExtra("groupid");      //接收群id

        SQL = "CREATE TABLE IF NOT EXISTS "+title
                +"_group_chat_list ("
                +"id integer primary key, "
                +"seq varchar(50) not null, "
                +"text varchar(255) not null, "
                +"user varchar(255) not null, "
                +"type int(2) not null)";

        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        initMsg();

        initCheck();

        conversation = TIMManager.getInstance().getConversation(          /**获取会话*/
                TIMConversationType.Group,      //会话类型：群组
                groupID);                       //群组 ID

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);
        recyclerView.scrollToPosition(msgList.size() - 1);              //将view定位到最后一行


        button.setOnClickListener(new View.OnClickListener() {           //点击发送消息
            @Override
            public void onClick(View view) {
                String string = editText.getText().toString().trim();          //获取需要发送的消息
                if(!"".equals(string)){
                    Msg msg = new Msg("user", string, Msg.SEND);
                    msgList.add(msg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);          //有新消息，刷新显示
                    recyclerView.scrollToPosition(msgList.size() - 1);              //将view定位到最后一行

                    /**发送消息*/
                    //构造一条消息
                    TIMMessage msg1 = new TIMMessage();
                    //添加文本内容
                    TIMTextElem elem = new TIMTextElem();
                    elem.setText(string);

                    //将elem添加到消息
                    if(msg1.addElement(elem) != 0) {
                        Log.d("tag", "addElement failed");
                        return;
                    }

                    //发送消息
                    conversation.sendMessage(msg1, new TIMValueCallBack<TIMMessage>() {//发送消息回调
                        @Override
                        public void onError(int code, String desc) {//发送消息失败
                            //错误码 code 和错误描述 desc，可用于定位请求失败原因
                            //错误码 code 含义请参见错误码表
                            Log.d("tag", "send message failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(TIMMessage msg) {//发送消息成功
                            Log.e("tag", "SendMsg ok");

                            initMsg();
                        }
                    });

                    editText.setText("");             //清空输入
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {         //设置显示更多功能
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {             //点击空白区域关闭键盘
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //关闭键盘
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                relativeLayout.setVisibility(View.GONE);

                button.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {              //点击文本框
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                relativeLayout.setVisibility(View.GONE);

                button.setVisibility(View.VISIBLE);         //显示发送按钮
                imageView.setVisibility(View.GONE);          //隐藏多功能按钮
                return false;
            }
        });

        faceRelativeLayout.setOnClickListener(new View.OnClickListener() {            //添加人脸签到
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, SignInActivity.class);
                intent.putExtra("type", FACE);
                intent.putExtra("group", title);
                startActivityForResult(intent, FACE);
            }
        });

        handsRelativeLayout.setOnClickListener(new View.OnClickListener() {            //添加手势签到
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, SignInActivity.class);
                intent.putExtra("type", HANDS);
                intent.putExtra("group", title);
                startActivityForResult(intent, HANDS);
            }
        });

        msgAdapter.setOnItemClickListener(new MsgAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {

            }
        });

        signLayout.setOnClickListener(new View.OnClickListener() {              //点击签到的入口
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, MainSignActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("name", Sign_Title);
                intent.putExtra("user", Sign_Create_User);
                if (type == HANDS){           //如果是手势签到，要额外的发送数据
                    intent.putExtra("password", Sign_Password);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {              //添加menu菜单
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            case R.id.share_group:
                Intent intent = new Intent(GroupActivity.this, ShareGroup.class);
                intent.putExtra("groupName", title);
                intent.putExtra("groupid", groupID);
                startActivityForResult(intent, SHARE);
                break;
            case R.id.group_info:
                Intent intent1 = new Intent(GroupActivity.this, GroupUserActivity.class);
                intent1.putExtra("groupName", title);
                intent1.putExtra("groupid", groupID);
                startActivityForResult(intent1, INFO);
                break;
            case 16908332:
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FACE || requestCode == HANDS){
            String password = data.getStringExtra("password");         //获取密码
            String name = data.getStringExtra("name");
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            int second = data.getIntExtra("second", 0);
            leftTime = hour*3600 + minute*60 + second;

            signName.setText(name);
            Sign_Title = name;
            Sign_Create_User = SharedPrefUtil.getUserName(getApplicationContext());
            Sign_Password = password;
        }

        switch (requestCode){
            case FACE:           //从创建人脸返回

                setTitle(title);
                if(resultCode == 0){
                    System.out.println("从人脸返回！！！");
                    type = FACE;

                    handler.postDelayed(update_thread, 1000);

                } else {
                    Toast.makeText(getApplicationContext(), "创建失败！请重试", Toast.LENGTH_SHORT).show();
                }

                break;
            case HANDS:           //从创建手势签到返回
                setTitle(title);
                if (resultCode == 0){       //返回值为0表示创建成功 1表示创建失败
                    System.out.println("从手势返回！！！");
                    type = HANDS;

                    handler.postDelayed(update_thread, 1000);

                } else {
                    Toast.makeText(getApplicationContext(), "创建失败！请重试", Toast.LENGTH_SHORT).show();
                }
                break;
            case SHARE:
                setTitle(title);
            case INFO:
                setTitle(title);
        }
    }


    Handler handler = new Handler();
    Runnable update_thread = new Runnable() {
        @Override
        public void run() {
            leftTime--;
            //LogUtil.e("leftTime="+leftTime);
            if (leftTime > 0) {

                signLayout.setVisibility(View.VISIBLE);
                //倒计时效果展示
                String formatLongToTimeStr = formatLongToTimeStr(leftTime);
                liftTime.setText(formatLongToTimeStr);
                System.out.println(formatLongToTimeStr);
                //每一秒执行一次
                handler.postDelayed(this, 1000);
            } else {//倒计时结束
                //处理业务流程
                signLayout.setVisibility(View.GONE);     //结束计时
                //发送消息，结束倒计时
            }
        }
    };


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("tag", "1----------onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leftTime = 0;
        handler.removeCallbacks(update_thread);
    }

    private String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue() ;
        if (second > 60) {
            minute = second / 60;   //取整
            second = second % 60;   //取余
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour+":"+minute+":"+second;
        return strtime;
    }

    public void init(){
        imageView = findViewById(R.id.ivAdd);
        relativeLayout = findViewById(R.id.bottom_layout);
        recyclerView = findViewById(R.id.rv_chat_list);

        editText = findViewById(R.id.et_content);
        button = findViewById(R.id.rv_sent_button);

        faceRelativeLayout = findViewById(R.id.rlFace);
        handsRelativeLayout = findViewById(R.id.rlHands);
        signLayout = findViewById(R.id.Sign_layout);

        signName = findViewById(R.id.Sign_name);
        liftTime = findViewById(R.id.lift_time);
    }

    private void initMsg(){

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data_chat", null, 2, SQL);       //向数据库插入数据
        databaseHelper.CreateTable();        //创建新表
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /**获取本地消息*/
        //获取会话扩展实例
        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupID);

        //获取此会话的消息
        con.getLocalMessage(20, //获取此会话最近的 10 条消息
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

                        for(int j = msgs.size() - 1; j >= 0; j--){
                            TIMMessage msg = msgs.get(j);
                            //lastMsg = msg;
                            //System.out.println(msg.toString());
                            //可以通过 timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                            Log.d("tag", "get msg: " + msg.timestamp() + " self: " + msg.isSelf() + " seq: " + msg.getSeq()+" "+msg.getSender());

                            for(int i = 0; i < msg.getElementCount(); ++i) {
                                TIMElem elem = msg.getElement(i);

                                //获取当前元素的类型
                                TIMElemType elemType = elem.getType();
                                Log.d("tag", "elem type: " + elemType.name());
                                if (elemType == TIMElemType.Text) {
                                    TIMTextElem textElem = (TIMTextElem)elem;
                                    System.out.println(textElem.getText());         //消息内容

                                    Cursor cursor = db.query(title +"_group_chat_list", null, "seq = ?", new String[]{String.valueOf(msg.getSeq())}, null, null, "id");
                                    if (cursor.getCount() == 0) {
                                        if(msg.isSelf() == true){       //判断是发出还是接收
                                            ContentValues values = new ContentValues();
                                            values.put("text", textElem.getText());        //内容
                                            values.put("seq", String.valueOf(msg.getSeq()));             //消息序列号
                                            values.put("user", msg.getSender());           //用户
                                            values.put("type", Msg.SEND);             //发送的消息

                                            db.insert(title +"_group_chat_list", null, values);

                                        } else {
                                            ContentValues values = new ContentValues();
                                            values.put("text", textElem.getText());        //内容
                                            values.put("seq", String.valueOf(msg.getSeq()));             //消息序列号
                                            values.put("user", msg.getSender());           //用户
                                            values.put("type", Msg.RECEIVE);             //发送的消息

                                            db.insert(title +"_group_chat_list", null, values);

                                        }
                                    }



                                }
                            }

                        }
                    }
                });


        /**从数据库查找数据*/
        System.out.println("创建"+SQL);
        Cursor cursor = db.query(title +"_group_chat_list", null, null, null, null, null, "id");
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){

            Msg msg = new Msg();
            msg.setContent(cursor.getString(2));
            msg.setUser(cursor.getString(3));
            msg.setType(cursor.getInt(4));

            msgList.add(msg);
            cursor.moveToNext();
        }
    }

    private void initCheck(){           //获取本群组的签到信息
        /**
         * 从本地数据库获取是否有本群的签到
         * */
        Calendar calendar = Calendar.getInstance();        //获取当前时间
        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.MONTH);
        calendar.get(Calendar.DATE);
        calendar.get(Calendar.HOUR_OF_DAY);       //24小时制
        calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.SECOND);
        Date curTime = calendar.getTime();        //当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        System.out.println("当前时间："+dateFormat.format(curTime));

        DatabaseHelper databaseHelper = new DatabaseHelper(this, "app_data", null, 1, com.example.application.ui.SQL.sql_create_sign_list);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("sign_list", null, "groupName = ? and deadline_time > ?", new String[]{title, String.valueOf(curTime.getTime() / 1000)}, null, null, "id");       //搜索可用的签到
        cursor.moveToFirst();
        while(!cursor.isAfterLast() && (cursor.getString(1) != null)){
            String title = cursor.getString(1);            //获取title
            String signtype = cursor.getString(2);
            String deadline_time = cursor.getString(3);
            String password = cursor.getString(4);
            String groupName = cursor.getString(5);
            String createUser = cursor.getString(6);
            int state = cursor.getInt(7);

            if (state != 1){           //不为1说明还未签到
                System.out.println(Long.parseLong(deadline_time));
                System.out.println(curTime.getTime() / 1000);
                long Time = Long.parseLong(deadline_time) - curTime.getTime() / 1000;
                leftTime = Time;

                signName.setText(title);
                Sign_Title = title;
                Sign_Create_User = createUser;
                type = Integer.parseInt(signtype);
                handler.postDelayed(update_thread, 1000);
            }
            System.out.println(title+" "+type+" "+deadline_time+" "+password+" "+groupName+" "+createUser+" "+state);

            cursor.moveToNext();
        }
    }



}
