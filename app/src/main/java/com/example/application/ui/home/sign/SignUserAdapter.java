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

public class SignUserAdapter extends ArrayAdapter<SignUser> {
    private int resourceId;

    public SignUserAdapter(Context context, int textViewResourceId, List<SignUser> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SignUser signUser = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        TextView userName = view.findViewById(R.id.check_user_userName);
        TextView realName = view.findViewById(R.id.check_user_realName);

        String user = signUser.getUserName();
        String real = signUser.getRealName();

        userName.setText(user);
        realName.setText(real);

        return view;
    }

    @Override
    public int getPosition(@Nullable SignUser item) {
        return super.getPosition(item);
    }
}
