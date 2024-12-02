package com.example.lzz_finaltask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
                NavigationUtils.navigateTo(MainActivity.this, ProfileActivity.class);
                return true;
            }
            return false;
        });


        // 设置点击事件
        newsAdapter.setOnItemClickListener((news, position) -> {
            // TODO: 处理新闻点击事件，跳转到新闻详情页
            Toast.makeText(this, "点击了：" + news.getTitle(), Toast.LENGTH_SHORT).show();
        });


    }

    private void initViews() {
        navigationView = findViewById(R.id.main_bottom_nav);
        navigationView.setSelectedItemId(R.id.navigation_home);
        recyclerView = findViewById(R.id.news_recycler_view);
        // 添加这一行，设置LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);
    }
    @Override
    public void onBackPressed() {
        // 最小化应用而不是关闭
        moveTaskToBack(true);
    }

    private void getNewsList(){
        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsList();
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if(body.getCode() == 0 && body.getData() !=null){
                    newsList = GsonUtil.parseList(body.getData(),News.class);
                    newsAdapter.setNewsList(newsList);
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this,"服务器繁忙",Toast.LENGTH_SHORT).show();
            }
        });
    }

}