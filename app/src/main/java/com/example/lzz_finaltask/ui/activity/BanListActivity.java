package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.BanRecord;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.BanListAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BanListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private View emptyView;
    private BanListAdapter adapter;
    private User admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ban_list);

        initViews();
        setupRecyclerView();
        loadBanList();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        admin = SharedPreferencesUtil.getUser(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BanListAdapter();
        recyclerView.setAdapter(adapter);

        // 设置解除封禁的点击事件
        adapter.setOnUnbanClickListener((bannedUser, position) -> {
            showUnbanConfirmDialog(bannedUser, position);
        });
    }

    private void loadBanList() {
        Call<BaseResponse> call = RetrofitManager.getApiService().getBannedUsers(admin.getUserId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    List<BanRecord> bannedUsers = GsonUtil.parseList(
                            response.body().getData(), BanRecord.class);
                    updateBanList(bannedUsers);
                } else {
                    Toast.makeText(BanListActivity.this,
                            "获取封禁列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(BanListActivity.this,
                        "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBanList(List<BanRecord> bannedUsers) {
        if (bannedUsers != null && !bannedUsers.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.setBanRecords(bannedUsers);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void showUnbanConfirmDialog(BanRecord bannedUser, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("解除封禁")
                .setMessage("确定要解除对用户 " + bannedUser.getUsername() + " 的封禁吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    unbanUser(bannedUser, position);
                })
                .show();
    }

    private void unbanUser(BanRecord bannedUser, int position) {
        Call<BaseResponse> call = RetrofitManager.getApiService().unbanUser(bannedUser.getUserId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    adapter.removeBanRecord(position);
                    Toast.makeText(BanListActivity.this,
                            "已解除封禁", Toast.LENGTH_SHORT).show();

                    // 如果列表为空，显示空状态
                    if (adapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(BanListActivity.this,
                            "解除封禁失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(BanListActivity.this,
                        "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}