package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth=FirebaseAuth.getInstance();

        BottomNavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedlistener);

        //default on start
        actionBar.setTitle("Users");
        UsersFragment fragment1=new UsersFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedlistener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");

                            ProfileFragment fragment2=new ProfileFragment();
                            FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                            return true;


                        case R.id.nav_users:
                            actionBar.setTitle("Users");
                            UsersFragment fragment1=new UsersFragment();
                            FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,fragment1,"");
                            ft1.commit();
                            return true;

                        


                    }
                    return false;
                }
            };

    private void checkuserstatus()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null)
        {



        }else
        {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkuserstatus();
        super.onStart();
    }


}
