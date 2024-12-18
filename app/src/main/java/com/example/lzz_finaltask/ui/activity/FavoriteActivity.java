package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsListAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favorite);
        
        initViews();
        setupRecyclerView();
        loadFavoriteNews();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_go_index_f).setOnClickListener(v -> {
            NavigationUtils.navigateWithClearTask(this, MainActivity.class);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsListAdapter = new NewsListAdapter(this);
        newsListAdapter.setPageType(NewsListAdapter.PAGE_TYPE_FAVORITE);
        recyclerView.setAdapter(newsListAdapter);
        
        // 设置点击事件
        newsListAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(FavoriteActivity.this,NewsDetailActivity.class,intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });
    }



    private void loadFavoriteNews() {
        Long userId = SharedPreferencesUtil.getUser(this).getUserId();
        Call<BaseResponse> call = RetrofitManager.getApiService().getFavoriteNews(userId);
        
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body != null && body.getCode() == 0) {
                    List<News> newsList = GsonUtil.parseList(body.getData(), News.class);
                    newsListAdapter.setNewsList(newsList);
                    if (newsList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContentState();
                    }
                } else {
                    Toast.makeText(FavoriteActivity.this,
                        "获取收藏列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, 
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
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteNews();
    }
}