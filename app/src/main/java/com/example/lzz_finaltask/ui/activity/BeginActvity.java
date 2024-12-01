package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

public class BeginActvity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 检查是否已登录
        if (SharedPreferencesUtil.isLoggedIn(this)) {
            // 已登录，直接进入主页
            NavigationUtils.navigateWithClearTask(this, MainActivity.class);
        } else {
            // 未登录，进入登录页
            NavigationUtils.navigateWithClearTask(this, LoginActivity.class);
        }
    }
}