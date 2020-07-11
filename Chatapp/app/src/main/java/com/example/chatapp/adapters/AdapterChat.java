package com.example.chatapp.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    FirebaseUser fuser;

    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
String messaage=chatList.get(position).getMessage();
String timestamp=chatList.get(position).getTimestamp();

        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime= DateFormat.format("dd/mm/yyyy hh:mm aa",cal).toString();

        holder.messageTv.setText(messaage);
        holder.timeTv.setText(datetime);

        if(position==chatList.size()-1)
        {
            if(chatList.get(position).isIsseen())
            {
                holder.isseenTv.setText("seen");

            }
            else {
                holder.isseenTv.setText("delivered");

            }
        }
        else
        {
            holder.isseenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fuser.getUid()))
        {
            return  MSG_TYPE_RIGHT;
        }
        else
        {
            return  MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView messageTv,timeTv,isseenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
            isseenTv=itemView.findViewById(R.id.seenTv);
        }
    }
}
