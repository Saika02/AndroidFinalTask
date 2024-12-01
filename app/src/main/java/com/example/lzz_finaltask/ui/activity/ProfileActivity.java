package com.example.lzz_finaltask.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {
    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvUserId;
    private MaterialButton btnLogout;
    private LinearLayout llFavorites;
    private LinearLayout llHistory;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        
        initViews();
        loadUserInfo();
        setClickListeners();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserId = findViewById(R.id.tv_user_id);
        btnLogout = findViewById(R.id.btn_logout);
        llFavorites = findViewById(R.id.ll_favorites);
        llHistory = findViewById(R.id.ll_history);
        bottomNavigationView = findViewById(R.id.person_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }

    private void loadUserInfo() {
        // 从SharedPreferences获取用户信息
        User user = SharedPreferencesUtil.getUser(ProfileActivity.this);
        tvUsername.setText(user.getUsername());
        tvUserId.setText("ID: " + user.getUserId());
    }

    private void setClickListeners() {
        // 我的收藏
        llFavorites.setOnClickListener(v -> {
            // TODO: 跳转到收藏页面
            Toast.makeText(this, "我的收藏", Toast.LENGTH_SHORT).show();
        });

        // 浏览历史
        llHistory.setOnClickListener(v -> {
            // TODO: 跳转到历史记录页面
            Toast.makeText(this, "浏览历史", Toast.LENGTH_SHORT).show();
        });
        // 退出登录
        btnLogout.setOnClickListener(v -> showLogoutDialog());


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                NavigationUtils.navigateWithClearTask(ProfileActivity.this, MainActivity.class);
                return true;
            }
            return false;
        });

    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定", (dialog, which) -> {
                SharedPreferencesUtil.clearLoginState(ProfileActivity.this);
                NavigationUtils.navigateWithClearTask(ProfileActivity.this,LoginActivity.class);
            })
            .show();
    }

} 