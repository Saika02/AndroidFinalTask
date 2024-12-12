package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeNewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private String newsType;
    private String typeDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_news_list);

        // 获取传入的新闻类型
        newsType = getIntent().getStringExtra("news_type");
        typeDesc = getIntent().getStringExtra("type_desc");

        initViews();
        loadNewsList();
    }

    private void initViews() {
        // 设置标题
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(typeDesc);

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);

        // 设置新闻点击事件
        newsAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(this, NewsDetailActivity.class, intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });

        // 初始化下拉刷新
        swipeRefresh = findViewById(R.id.swipe_refresh);
        // 设置刷新时的颜色变化
        swipeRefresh.setColorSchemeResources(
            R.color.primary,
            R.color.secondary
        );
        // 设置下拉刷新监听器
        swipeRefresh.setOnRefreshListener(this::loadNewsList);
    }

    private void loadNewsList() {
        // 如果不是由下拉触发的，显示刷新动画
        if (!swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(true);
        }

        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsByType(newsType);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                // 隐藏刷新动画
                swipeRefresh.setRefreshing(false);
                
                BaseResponse body = response.body();
                if (body != null && body.getCode() == 0) {
                    List<News> newsList = GsonUtil.parseList(body.getData(), News.class);
                    newsAdapter.setNewsList(newsList);
                } else {
                    Toast.makeText(TypeNewsActivity.this,
                        "获取新闻列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                // 隐藏刷新动画
                swipeRefresh.setRefreshing(false);
                Toast.makeText(TypeNewsActivity.this,
                    "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}