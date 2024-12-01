package com.example.lzz_finaltask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initViews();
        setClickListeners();
    }

    private void setClickListeners() {
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // 已经在首页，返回true
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 跳转到个人页面
                NavigationUtils.navigateWithClearTask(MainActivity.this, ProfileActivity.class);
                return true;
            }
            return false;
        });
    }

    private void initViews() {
        navigationView = findViewById(R.id.main_bottom_nav);
        navigationView.setSelectedItemId(R.id.navigation_home);
    }


}