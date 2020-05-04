package com.example.application.ui.home;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.R;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.home.group.Group;
import com.example.application.ui.home.group.GroupActivity;
import com.example.application.ui.home.group.GroupAdapter;
import com.example.application.ui.home.msg.Msg;
import com.example.application.ui.home.scan.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupSystemElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.imsdk.TIMGroupSystemElemType.TIM_GROUP_SYSTEM_ADD_GROUP_REQUEST_TYPE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView newTextView;         //显示新消息红点

    private List<Group> GroupList = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private String AddGroupID;
    private String AddGroupUser;

    public static final int SEARCH = 101;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initList();          //显示已加入的群组

        System.out.println("home被创建了");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.home_list);
        swipeRefreshLayout = root.findViewById(R.id.swipe_list);
        newTextView = root.findViewById(R.id.new_message_view);

        setHasOptionsMenu(true);             /**添加右上角menu*/
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        Group group = new Group(R.drawable.default_head, "efaf&jdf","all[i].trim()", "test");
        GroupList.add(group);

        groupAdapter = new GroupAdapter(getContext(), R.layout.group_item, GroupList);
        groupAdapter.notifyDataSetChanged();
        listView.setAdapter(groupAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {       //点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group group = GroupList.get(i);
                //Toast.makeText(getContext(), group.getGroupName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra("name", group.getGroupName());          //发送群组名称
                intent.putExtra("groupid", group.getGroupID());          //发送群组id
                startActivity(intent);

                View view1 = listView.getChildAt(listView.getFirstVisiblePosition());
                view1.findViewById(R.id.new_message_view).setVisibility(View.VISIBLE);

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
                            groupAdapter.notifyDataSetChanged();
                            listView.setAdapter(groupAdapter);
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

        /**
         * 获取加入的群组列表
         * */
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

                    Group group = new Group(R.drawable.default_head, info.getGroupId(), info.getGroupName(), "test");
                    GroupList.add(group);

                    Log.d("tag", "group id: " + info.getGroupId() +
                            " group name: " + info.getGroupName() +
                            " group type: " + info.getGroupType());
                }
            }
        };

        //获取已加入的群组列表
        TIMGroupManager.getInstance().getGroupList(cb);


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
                String[] info = group_info.split("$");         //分割开群名称与群id
                Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                intent.putExtra("groupName", info[0]);
                intent.putExtra("groupid", info[1]);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getUserAddGroup(){           //获取用户加群信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        /**接收申请加群消息*/
                        TIMGroupSystemElem timGroupSystemElem = new TIMGroupSystemElem();
                        TIMGroupSystemElemType timGroupSystemElemType = timGroupSystemElem.getSubtype();         //获取申请加群
                        if(timGroupSystemElemType.equals(TIM_GROUP_SYSTEM_ADD_GROUP_REQUEST_TYPE)){
                            AddGroupID = timGroupSystemElem.getGroupId();         //获取群组ID
                            AddGroupUser = timGroupSystemElem.getOpUser();         //获取申请加群人
                            String url = "https://120.26.172.16:8443/AndroidTest/AddUserToGroup?username="+AddGroupUser+"&groupid="+AddGroupID;
                            HttpsUtil.getInstance().get(url, new HttpsUtil.OnRequestCallBack() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.d("log", "加入群组失败");
                                }

                                @Override
                                public void onFail(Exception e) {
                                    Log.d("log", "加入群组成功");
                                }
                            });
                        }

                        Log.i("info", "获取加群信息");

                        /**接收新消息*/
                        //设置消息监听器，收到新消息时，通过此监听器回调
                        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
                            @Override
                            public boolean onNewMessages(List<TIMMessage> list) {          //收到新消息
                                newTextView.setVisibility(View.VISIBLE);
                                Log.d("log", "获取新消息");

                                for(int i = list.size() - 1; i >= 0; --i){
                                    TIMMessage message = list.get(i);
                                    TIMConversation conversation = message.getConversation();
                                    String groupId = conversation.getPeer();          //获取群组ID

                                    for(int j = 0; j< message.getElementCount(); ++j) {
                                        TIMElem elem = message.getElement(i);

                                        //获取当前元素的类型
                                        TIMElemType elemType = elem.getType();
                                        Log.d("tag", "elem type: " + elemType.name());
                                        if (elemType == TIMElemType.Text) {
                                            TIMTextElem textElem = (TIMTextElem)elem;
                                            System.out.println(textElem.getText());         //消息内容
                                        }
                                    }

                                    View view = listView.findViewWithTag(groupId);       //获取要改变的ListView的item
                                    view.findViewById(R.id.new_message_view).setVisibility(View.VISIBLE);         //显示小红点
                                    //view.findViewById(R.id.group_describe);
                                }
                                return false;     //返回true将终止回调链，不再调用下一个新消息监听器
                            }
                        });

                        Thread.sleep(10000);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateItemView(int position) {
        //得到第一个可显示控件的位置，
        int visiblePosition = listView.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        int index = position - visiblePosition;
        if (index >= 0) {
            //得到要更新的item的view
            View view = listView.getChildAt(index);
            //从view中取得holder
            //groupAdapter.ViewHolder holder = (groupAdapter.ViewHolder) view.getTag();
            //更改状态
            //holder.textview.setText("测试数据");
        }
    }

}