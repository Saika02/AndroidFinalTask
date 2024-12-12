package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private View emptyView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        initViews();
        setupRecyclerView();
        loadHistories();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_clear).setOnClickListener(v -> showClearDialog());
        findViewById(R.id.btn_go_index_h).setOnClickListener(
                v -> NavigationUtils.navigateWithClearTask(HistoryActivity.this, MainActivity.class));

        user = SharedPreferencesUtil.getUser(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(this, NewsDetailActivity.class, intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });
    }

    private void loadHistories() {

        Long userId = user.getUserId();
        Call<BaseResponse> call = RetrofitManager.getApiService().getBrowsingHistories(userId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body != null && body.getCode() == 0) {
                    List<News> newsList = GsonUtil.parseList(body.getData(), News.class);
                    newsAdapter.setNewsList(newsList);

                    if (newsList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContentState();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this,
                            "获取浏览历史失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this,
                        "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showContentState() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showClearDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清空浏览历史")
                .setMessage("确定要清空所有浏览历史吗？")
                .setPositiveButton("确定", (dialog, which) -> clearHistories())
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearHistories() {
        Long userId = user.getUserId();
        Call<BaseResponse> call = RetrofitManager.getApiService().clearHistories(userId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body != null && body.getCode() == 0) {
                    Toast.makeText(HistoryActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                    loadHistories(); // 重新加载数据
                } else {
                    Toast.makeText(HistoryActivity.this, "清除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistories();
    }
}