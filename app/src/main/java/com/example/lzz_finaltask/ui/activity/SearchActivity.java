package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsListAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        // 获取传入的搜索关键词
        currentKeyword = getIntent().getStringExtra("keyword");
        
        initViews();
        setupSearch();
        
        // 如果有关键词就直接搜索
        if (!TextUtils.isEmpty(currentKeyword)) {
            etSearch.setText(currentKeyword);
            searchNews(currentKeyword);
        }
    }

    private void initViews() {
        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // 搜索框
        etSearch = findViewById(R.id.et_search);
        
        // 搜索按钮
        findViewById(R.id.btn_search).setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                searchNews(keyword);
            }
            else Toast.makeText(SearchActivity.this,"搜索框不能为空",Toast.LENGTH_SHORT).show();
        });

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsListAdapter = new NewsListAdapter(this);
        recyclerView.setAdapter(newsListAdapter);

        newsListAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(this, NewsDetailActivity.class, intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.primary);
        swipeRefresh.setOnRefreshListener(() -> searchNews(currentKeyword));
    }

    private void setupSearch() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    searchNews(keyword);
                }
                else{
                    Toast.makeText(SearchActivity.this,"搜索框不能为空",Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            return false;
        });
    }

    private void searchNews(String keyword) {
        currentKeyword = keyword;
        
        if (!swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(true);
        }

        Call<BaseResponse> call = RetrofitManager.getApiService().searchNews(keyword);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                swipeRefresh.setRefreshing(false);

                BaseResponse body = response.body();
                if (body != null && body.getCode() == 0) {
                    List<News> newsList = GsonUtil.parseList(body.getData(), News.class);
                    newsListAdapter.setNewsList(newsList);
                } else {
                    Toast.makeText(SearchActivity.this,
                            "搜索失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(SearchActivity.this,
                        "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}