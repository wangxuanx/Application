package com.example.application.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.application.MainActivity;
import com.example.application.R;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeaveActivity extends AppCompatActivity {
    private LinearLayout typeLayout;
    private LinearLayout beginLayout;
    private LinearLayout endLayout;
    private LinearLayout checkLayout;

    private TextView typeText;
    private TextView beginText;
    private TextView endText;
    private TextView checkText;

    private EditText editText;

    private Button button;

    private ArrayList<String> optionsItems = new ArrayList<>();           //请假类型List
    private ArrayList<CheckBean> options1Items = new ArrayList<>();
    private ArrayList<String> options2Items = new ArrayList<>();
    private ArrayList<String> groupList = new ArrayList<>();        //群组列表
    private ArrayList<ArrayList<String>> userList = new ArrayList<>();         //用户列表

    private String type;            //请假类型
    private String beginTime;        //起始时间
    private String endTime;         //终止时间
    private String otherInfo;         //请假备注
    private String checkUser;         //审核人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        setTitle("请假条");

        init();       //初始化控件

        initData();         //初始化选择数据

        typeLayout.setOnClickListener(new View.OnClickListener() {         //设置请假类型
            @Override
            public void onClick(View view) {

                OptionsPickerView pickerView = new OptionsPickerBuilder(LeaveActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        String tx = optionsItems.get(options1);
                                /*+ options2Items.get(options1).get(option2)
                                + options3Items.get(options1).get(option2).get(options3).getPickerViewText();*/
                        type = tx;
                        typeText.setText(tx);          //设置请假类型
                    }
                }).setTitleText("请假类型")
                        .setSubmitColor(Color.rgb(00,85,77))
                        .setCancelColor(Color.rgb(00,85,77))
                        .build();

                pickerView.setPicker(optionsItems);
                pickerView.show();
            }
        });

        beginLayout.setOnClickListener(new View.OnClickListener() {          //设置起始时间
            @Override
            public void onClick(View view) {

                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(LeaveActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                        beginText.setText(longToDate(date.getTime())+"时");
                        beginTime = longToDate(date.getTime());
                    }
                }).setTitleText("起始时间")
                        .setType(new boolean[]{true, true, true, true, false, false})// 默认全部显示
                        .setSubmitColor(Color.rgb(00,85,77))
                        .setCancelColor(Color.rgb(00,85,77))
                        .build();

                pvTime.show();
            }
        });

        endLayout.setOnClickListener(new View.OnClickListener() {             //设置结束时间
            @Override
            public void onClick(View view) {
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(LeaveActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                        endText.setText(longToDate(date.getTime())+"时");
                        endTime = longToDate(date.getTime());
                    }
                }).setTitleText("起始时间")
                        .setType(new boolean[]{true, true, true, true, false, false})// 默认全部显示
                        .setSubmitColor(Color.rgb(00,85,77))
                        .setCancelColor(Color.rgb(00,85,77))
                        .build();

                pvTime.show();
            }
        });

        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OptionsPickerView pickerView = new OptionsPickerBuilder(LeaveActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        String tx = options1Items.get(options1).getPickerViewText()+"-"
                                + options1Items.get(options1).getUserList().get(options2);
                                /*+ options3Items.get(options1).get(option2).get(options3).getPickerViewText();*/
                        String[] info = tx.split("-");
                        checkUser = info[1];
                        checkText.setText(tx);          //设置受理人
                    }
                }).setTitleText("受理人")
                        .setSubmitColor(Color.rgb(00,85,77))
                        .setCancelColor(Color.rgb(00,85,77))
                        .build();

                pickerView.setPicker(groupList, userList);
                pickerView.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {          //点击按钮向服务器发送请求信息
            @Override
            public void onClick(View view) {
                otherInfo = editText.getText().toString().trim();

                System.out.println(type+" "+beginTime+" "+endTime+" "+otherInfo+" "+checkUser);

                finish();
            }
        });
    }

    private static String longToDate(long lo){           //long转化为时形式

        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH");         //"yyyy-MM-dd HH:mm:ss"
        return sd.format(date);
    }

    private void init(){
        typeLayout = findViewById(R.id.leave_type_layout);
        beginLayout = findViewById(R.id.leave_begin_time_layout);
        endLayout = findViewById(R.id.leave_end_time_layout);
        checkLayout = findViewById(R.id.leave_check_user_layout);

        typeText = findViewById(R.id.leave_type_text);
        beginText = findViewById(R.id.leave_begin_time);
        endText = findViewById(R.id.leave_end_time);
        checkText = findViewById(R.id.leave_check_user);

        editText = findViewById(R.id.leave_more_info);

        button = findViewById(R.id.leave_confirm);
    }

    private void initData(){
        optionsItems.add("事假");
        optionsItems.add("病假");
        optionsItems.add("其他假项");


        /**
         * 获取加入的群
         * **/
        TIMValueCallBack<List<TIMGroupBaseInfo>> cb = new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.e("tag", "get gruop list failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {//参数返回各群组基本信息
                Log.d("tag", "get gruop list succ");

                for(TIMGroupBaseInfo info : timGroupInfos) {


                    /**
                     * 获取群用户
                     * **/
                    //创建回调
                    TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>> () {
                        @Override
                        public void onError(int code, String desc) {
                        }

                        @Override
                        public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息
                            for(TIMGroupMemberInfo info : infoList) {
                                options2Items.add(info.getUser());

                                Log.d("tag", "user: " + info.getUser() +
                                        "join time: " + info.getJoinTime() +
                                        "role: " + info.getRole());
                            }
                        }
                    };

                    //获取群组成员信息
                    TIMGroupManager.getInstance().getGroupMembers(
                            info.getGroupId(), //群组 ID
                            cb);     //回调
                    /**
                     *
                     * */
                    CheckBean checkBean = new CheckBean();

                    checkBean.setUserList(options2Items);       //用户导入list
                    checkBean.setGroupName(info.getGroupName());
                    groupList.add(info.getGroupName());
                    options1Items.add(checkBean);       //加入list中

                    userList.add(options2Items);

                    options2Items.clear();        //清空options2Items以加入下一个用户列表

                    Log.d("tag", "group id: " + info.getGroupId() +
                            " group name: " + info.getGroupName() +
                            " group type: " + info.getGroupType());
                }
            }
        };
        //获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);
    }
}
