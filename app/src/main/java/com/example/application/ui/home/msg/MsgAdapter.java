package com.example.application.ui.home.msg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        ImageView leftSignView;
        ImageView rightSignView;

        TextView leftText;
        TextView rightText;
        TextView leftUserText;
        TextView rightUserText;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_message_layout);
            rightLayout = view.findViewById(R.id.right_message_layout);

            leftImage = view.findViewById(R.id.left_message_head);
            rightImage = view.findViewById(R.id.right_message_head);


            leftText = view.findViewById(R.id.left_message_content);
            rightText = view.findViewById(R.id.right_message_content);
            leftUserText = view.findViewById(R.id.left_message_user);
            rightUserText = view.findViewById(R.id.right_message_user);
        }
    }

    public MsgAdapter(List<Msg> msgs){
        msgList = msgs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });

        Msg msg = msgList.get(position);

        if(msg.getType() == Msg.RECEIVE){              //收到消息显示
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightUserText.setVisibility(View.GONE);
            holder.leftText.setText(msg.getContent());
            holder.leftUserText.setText(msg.getUser());
        } else if (msg.getType() == Msg.SEND){           //发送信息显示
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftUserText.setVisibility(View.GONE);
            holder.rightText.setText(msg.getContent());
            holder.rightUserText.setText(msg.getUser());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(View v, int position);
    }

    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
