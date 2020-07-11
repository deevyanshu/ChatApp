package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.chatapp.adapters.Adapteruser;
import com.example.chatapp.models.Modeluser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

RecyclerView recyclerView;
Adapteruser adapteruser;
List<Modeluser> userlist;
FirebaseAuth firebaseAuth;
    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=view.findViewById(R.id.users_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseAuth=FirebaseAuth.getInstance();

        userlist=new ArrayList<>();

        getallusers();
    return view;
    }

    private void getallusers() {
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Modeluser modeluser=ds.getValue(Modeluser.class);
                    if(!modeluser.getUid().equals(fuser.getUid()))
                    {
                        userlist.add(modeluser);
                    }

                    adapteruser=new Adapteruser(getActivity(),userlist);

                    recyclerView.setAdapter(adapteruser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkuserstatus()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null)
        {



        }else
        {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim()))
                {
                    searchusers(query);
                }else
                {
                    getallusers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim()))
                {
                    searchusers(newText);
                }else
                {
                    getallusers();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    private void searchusers(final String newText) {
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Modeluser modeluser=ds.getValue(Modeluser.class);
                    if(!modeluser.getUid().equals(fuser.getUid()))
                    {
                        if(modeluser.getName().toLowerCase().contains(newText.toLowerCase())||modeluser.getEmail().toLowerCase().contains(newText.toLowerCase()))
                        {
                            userlist.add(modeluser);

                        }
                    }

                    adapteruser=new Adapteruser(getActivity(),userlist);
                    adapteruser.notifyDataSetChanged();

                    recyclerView.setAdapter(adapteruser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
