package com.example.application.ui.home.sign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.R;

import java.util.List;

public class SignAdapter extends ArrayAdapter<Sign> {
    private int resourceId;

    public SignAdapter(Context context, int textViewResourceId, List<Sign> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Sign sign = getItem(position);
        String signTitle;

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView title = view.findViewById(R.id.check_item_title);
        TextView signName = view.findViewById(R.id.check_item_name);
        TextView createName = view.findViewById(R.id.check_item_create);
        TextView timeText = view.findViewById(R.id.check_create_time);

        if (sign.getType() == 100) {          //FACE
            signTitle = "人脸签到";
        } else {           //HANDS
            signTitle = "手势签到";
        }

        title.setText(signTitle);
        signName.setText(sign.getTitle());
        createName.setText(sign.getUser());
        timeText.setText(sign.getTime());

        return view;
    }

    @Override
    public int getPosition(@Nullable Sign item) {
        return super.getPosition(item);
    }
}
