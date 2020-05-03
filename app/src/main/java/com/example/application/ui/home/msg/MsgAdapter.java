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
        RelativeLayout leftLayoutSign;         //左边签到
        RelativeLayout rightLayoutSign;         //右边签到

        CircleImageView leftImage;
        CircleImageView rightImage;
        CircleImageView leftImageSign;
        CircleImageView rightImageSign;

        ImageView leftSignView;
        ImageView rightSignView;

        TextView leftText;
        TextView rightText;
        TextView leftSignText;
        TextView rightSignText;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_message_layout);
            rightLayout = view.findViewById(R.id.right_message_layout);
            leftLayoutSign = view.findViewById(R.id.left_sign_layout);
            rightLayoutSign = view.findViewById(R.id.right_sign_layout);

            leftImage = view.findViewById(R.id.left_message_head);
            rightImage = view.findViewById(R.id.right_message_head);
            leftImageSign = view.findViewById(R.id.left_sign_head);
            rightImageSign = view.findViewById(R.id.right_sign_head);

            leftSignView = view.findViewById(R.id.left_sign_image);
            rightSignView = view.findViewById(R.id.right_sign_image);

            leftText = view.findViewById(R.id.left_message_content);
            rightText = view.findViewById(R.id.right_message_content);
            leftSignText = view.findViewById(R.id.left_sign_text);
            rightSignText = view.findViewById(R.id.right_sign_text);
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
            holder.leftText.setText(msg.getContent());
            /**签到item消失*/
            holder.leftLayoutSign.setVisibility(View.GONE);
            holder.rightLayoutSign.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.SEND){           //发送信息显示
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightText.setText(msg.getContent());
            /**签到item消失*/
            holder.leftLayoutSign.setVisibility(View.GONE);
            holder.rightLayoutSign.setVisibility(View.GONE);
        } else if (msg.getType() == Msg.RECEIVE_SIGN){      //收到签到显示
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayoutSign.setVisibility(View.GONE);

            if(msg.getContent().equals("人脸签到")){
                holder.leftSignView.setImageResource(R.drawable.face);
            } else {
                holder.leftSignView.setImageResource(R.drawable.hands);
            }
            holder.leftSignText.setText(msg.getContent());
        } else if (msg.getType() == Msg.SEND_SIGN){        //发送签到
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftLayoutSign.setVisibility(View.GONE);

            if(msg.getContent().equals("人脸签到")){
                holder.rightSignView.setImageResource(R.drawable.face);
            } else {
                holder.rightSignView.setImageResource(R.drawable.hands);
            }
            holder.rightSignText.setText(msg.getContent());
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
