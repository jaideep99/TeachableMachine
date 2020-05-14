package com.example.teachablemachine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    private ViewPager loginpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        loginpager = (ViewPager) findViewById(R.id.login_pager);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),LoginActivity.this);
        loginpager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(loginpager);
    }
}
