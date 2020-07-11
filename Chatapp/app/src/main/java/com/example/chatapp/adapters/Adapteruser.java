package com.example.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.R;
import com.example.chatapp.models.Modeluser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapteruser extends RecyclerView.Adapter<Adapteruser.Myholder> {

    Context context;
    List<Modeluser> userlist;

    public Adapteruser(Context context, List<Modeluser> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);


        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
      final String hisUid=userlist.get(position).getUid();
       String userimage =userlist.get(position).getImage();
       String username=userlist.get(position).getName();
        final String useremail=userlist.get(position).getEmail();

        holder.mnameTv.setText(username);
        holder.mEmailTv.setText(useremail);
        try
        {
            Picasso.get().load(userimage).placeholder(R.drawable.ic_default_img).into(holder.mavatarIv);
        }catch (Exception e)
        {


        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class Myholder extends RecyclerView.ViewHolder
    {
        ImageView mavatarIv;
        TextView mnameTv,mEmailTv;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            mavatarIv=itemView.findViewById(R.id.avatarIv);
            mnameTv=itemView.findViewById(R.id.nameTv);
            mEmailTv=itemView.findViewById(R.id.emailTv);
        }
    }
}
