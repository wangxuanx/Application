package com.example.application.ui.home.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private int UserId;
    public UserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        UserId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(UserId, null);
        System.out.println("用户id： "+position);
        if(position == 0){
            ImageView imageView = (ImageView) view.findViewById(R.id.group_belong);
            imageView.setVisibility(View.VISIBLE);
        }
        ImageView UserImage = (ImageView) view.findViewById(R.id.user_list_head);
        TextView UserName = (TextView) view.findViewById(R.id.user_list_name);
        TextView UserReal = (TextView) view.findViewById(R.id.user_list_real);
        UserImage.setImageResource(user.getUserHead());
        UserName.setText(user.getUserName());
        UserReal.setText(user.getRealName());
        return view;
    }
}
