package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.adapters.AdapterChat;
import com.example.chatapp.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class ChatActivity extends AppCompatActivity {
Toolbar toolbar;
RecyclerView recyclerView;
ImageButton send;
ImageView profilview;
TextView nameTv;
EditText message;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    String hisUid;
    String myUid;
    String stringmessage;



    ValueEventListener seenlistener;
    DatabaseReference userRefForseen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.chat_recycler);
        profilview=findViewById(R.id.profileIv);
        nameTv=findViewById(R.id.nameTv);
        message=findViewById(R.id.messageEt);
        send=findViewById(R.id.sendbtn);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef=firebaseDatabase.getReference("users");




        Query userquery=usersDbRef.orderByChild("uid").equalTo(hisUid);

        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String name=""+ds.child("name").getValue();
                    String image=""+ds.child("image").getValue();
                    nameTv.setText(name);
                    try{
                        Picasso.get().load(image).placeholder(R.drawable.ic_default_white).into(profilview);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_default_img).into(profilview);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message1=message.getText().toString().trim();
                if(TextUtils.isEmpty(message1))
                {
                    Toast.makeText(ChatActivity.this,"cannot send",Toast.LENGTH_SHORT).show();
                }else
                {
                    sendmessage(message1);
                }
            }
        });
readmessage();

seenmessage();

    }

    private void seenmessage() {
        userRefForseen =FirebaseDatabase.getInstance().getReference("chats");
        seenlistener=userRefForseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid))
                    {
                        HashMap<String,Object> hasSeen=new HashMap<>();
                        hasSeen.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readmessage()
    {
        chatList=new ArrayList<>();
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid))
                    {
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(ChatActivity.this,chatList);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendmessage(String message1) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message1);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isseen",false);
        databaseReference.child("chats").push().setValue(hashMap);

        message.setText("");
    }



    private void checkuserstatus()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null)
        {
          myUid=user.getUid();


        }else
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkuserstatus();
        super.onStart();
    }

    @Override
    protected void onPause() {

        super.onPause();
        userRefForseen.removeEventListener(seenlistener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout)
        {
            firebaseAuth.signOut();
            checkuserstatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
