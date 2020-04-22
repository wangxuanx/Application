package com.example.application.ui.home.msg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> msgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout leftLayout;
        RelativeLayout rightLayout;

        CircleImageView leftImage;
        CircleImageView rightImage;

        TextView leftText;
        TextView rightText;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_message_layout);
            rightLayout = view.findViewById(R.id.right_message_layout);
            leftImage = view.findViewById(R.id.left_message_head);
            rightImage = view.findViewById(R.id.right_message_head);
            leftText = view.findViewById(R.id.left_message_content);
            rightText = view.findViewById(R.id.right_message_content);
        }
    }

    public MsgAdapter(List<Msg> msgs){
        msgList = msgs;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = msgList.get(position);

        if(msg.getType() == Msg.RECEIVE){              //收到消息显示
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftText.setText(msg.getContent());
        } else if (msg.getType() == Msg.SEND){           //发送信息显示
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightText.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
