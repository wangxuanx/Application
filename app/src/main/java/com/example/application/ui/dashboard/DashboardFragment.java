package com.example.application.ui.dashboard;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

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
                startActivityForResult(intent, 0);
            }
        });

        return root;
    }

    private void init(View root){
        floatActionButton = root.findViewById(R.id.circle_button);
        listView = root.findViewById(R.id.leave_list);
    }

    private void initData(){
        Check check = new Check(1,"我的请假条", "事假", "2020-4-1 12", "2020-4-3 12" ,"待审核");
        checkList.add(check);
        Check check1 = new Check(2,"待批准请假条", "病假", "2020-4-14 12", "2020-4-18 12" ,"待审核");
        checkList.add(check1);
    }
}