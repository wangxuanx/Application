package com.example.application.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.barnettwong.dragfloatactionbuttonlibrary.view.DragFloatActionButton;
import com.example.application.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private DragFloatActionButton floatActionButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(root);

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
    }
}