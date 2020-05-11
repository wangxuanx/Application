package com.example.application.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.R;
import com.example.application.ui.home.group.Group;

import java.util.List;

public class CheckAdapter extends ArrayAdapter<Check> {
    private int resourceId;
    /**
     *context:当前活动上下文
     *textViewResourceId:ListView子项布局的ID
     *objects：要适配的数据
     */
    public CheckAdapter(Context context, int textViewResourceId,
                        List<Check> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    /**
     * LIstView中每一个子项被滚动到屏幕的时候调用
     * position：滚到屏幕中的子项位置，可以通过这个位置拿到子项实例
     * convertView：之前加载好的布局进行缓存
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Check check = getItem(position);  //获取当前项的Fruit实例
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView Title = view.findViewById(R.id.check_title);
        TextView Type = view.findViewById(R.id.check_type);
        TextView User = view.findViewById(R.id.check_user);
        TextView BeginTime = view.findViewById(R.id.beginTime);
        TextView EndTime = view.findViewById(R.id.endTime);
        TextView State = view.findViewById(R.id.check_state);
        view.setTag(check.getId());           //设置view的Tag
        Title.setText(check.getTitle());
        Type.setText(check.getType());
        User.setText(check.getUser());
        BeginTime.setText(check.getBeginTime());
        EndTime.setText(check.getEndTime());
        State.setText(check.getState());
        
        return view;
    }

    @Override
    public int getPosition(@Nullable Check item) {
        return super.getPosition(item);
    }
}
