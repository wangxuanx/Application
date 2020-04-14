package com.example.application.ui.home;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.http.HttpUtil;
import com.example.application.http.HttpsUtil;
import com.example.application.http.SharedPrefUtil;
import com.example.application.ui.Group.GroupActivity;
import com.example.application.ui.scan.ScanActivity;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView textView;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Group> GroupList = new ArrayList<>();
    private GroupAdapter groupAdapter;

    public static final int SEARCH = 101;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.home_list);
        swipeRefreshLayout = root.findViewById(R.id.swipe_list);

        setHasOptionsMenu(true);             /**添加右上角menu*/
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));

        new Thread(new Runnable() {
            @Override
            public void run() {
                initList();          //显示已加入的群组
            }
        }).start();

        Group group = new Group(R.drawable.default_head, "all[i].trim()", "test");
        GroupList.add(group);

        groupAdapter = new GroupAdapter(getContext(), R.layout.group_item, GroupList);
        listView.setAdapter(groupAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {       //点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group group = GroupList.get(i);
                //Toast.makeText(getContext(), group.getGroupName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), GroupActivity.class);
                intent.putExtra("name", group.getGroupName());          //发送群组名称
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {            //下拉刷新列表
            @Override
            public void onRefresh() {

                for(int i = 0; i <= GroupList.size(); i++){
                    GroupList.remove(i);
                }

                initList();

                if(GroupList.size() != 0){
                    groupAdapter = new GroupAdapter(getContext(), R.layout.group_item, GroupList);
                    listView.setAdapter(groupAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

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
            case R.id.face_to_face:            //面对面建群
                Toast.makeText(getContext(), "面对面", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initList() {             //调用api来获取已经加入的群组
        String user = SharedPrefUtil.getUserName(getContext());
        System.out.println("用户" + user);
        String path = "https://120.26.172.16:8443/AndroidTest/GetUserGroup?user=" + user;       //098F6BCD4621D373CADE4E832627B4F6
        try {
            HttpsUtil.getInstance().get(path, new HttpsUtil.OnRequestCallBack() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("群组: " + s);
                    //Gson gson = new Gson();
                    //GroupBean groupBean = gson.fromJson(s, GroupBean.class);        //解析json数据
                    //String list = groupBean.getGroup_list();             //获取到搜索到到列表
                    String[] all = s.split(",");        //将字符分割开

                    for (int i = 0; i < all.length; i++) {
                        System.out.println("第" + (i + 1) + "个：" + all[i].trim());
                        Group group = new Group(R.drawable.default_head, all[i].trim(), "test");
                        GroupList.add(group);
                    }

                }

                @Override
                public void onFail(Exception e) {
                    Log.e("error", "初始化错误!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "扫描失败！！", Toast.LENGTH_LONG).show();
            } else {
                textView.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}