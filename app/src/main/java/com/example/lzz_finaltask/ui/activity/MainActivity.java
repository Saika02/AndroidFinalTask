package com.example.lzz_finaltask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private RecyclerView recyclerView;
    private List<News> newsList;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initViews();
        setClickListeners();
        getNewsList();
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
            } else if (itemId == R.id.navigation_explore) {
                NavigationUtils.navigateWithClearTask(MainActivity.this, DiscoveryActivity.class);
            }
            return false;
        });


        // 设置点击事件
        newsAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(MainActivity.this, NewsDetailActivity.class, intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });

        swipeRefresh.setOnRefreshListener(this::getNewsList);
    }

    private void initViews() {
        navigationView = findViewById(R.id.main_bottom_nav);
        navigationView.setSelectedItemId(R.id.navigation_home);
        recyclerView = findViewById(R.id.news_recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);


        // 初始化SwipeRefreshLayout
        swipeRefresh = findViewById(R.id.swipe_refresh);
        // 设置刷新时的颜色变化
        swipeRefresh.setColorSchemeResources(
                R.color.primary,
                R.color.secondary
        );
    }


    private void getNewsList() {
        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsList();
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                swipeRefresh.setRefreshing(false);

                BaseResponse body = response.body();
                if (body.getCode() == 0 && body.getData() != null) {
                    newsList = GsonUtil.parseList(body.getData(), News.class);
                    newsAdapter.setNewsList(newsList);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

                swipeRefresh.setRefreshing(false);
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "服务器繁忙", Toast.LENGTH_SHORT).show();
            }
        });
    }

}