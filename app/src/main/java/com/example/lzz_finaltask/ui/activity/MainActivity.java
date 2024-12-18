package com.example.lzz_finaltask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsListAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private RecyclerView recyclerView;
    private List<News> newsList;
    private NewsListAdapter newsListAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ImageView searchImg;
    private EditText searchLine;


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
        newsListAdapter.setOnItemClickListener((news, position) -> {
            NavigationUtils.navigateWith(MainActivity.this, NewsDetailActivity.class, intent -> {
                intent.putExtra("news_id", news.getNewsId());
                intent.putExtra("news_title", news.getTitle());
                intent.putExtra("news_date", news.getPublishTime());
            });
        });

        swipeRefresh.setOnRefreshListener(this::getNewsList);

        searchImg.setOnClickListener(v -> {
            String keyword = searchLine.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                NavigationUtils.navigateWith(MainActivity.this, SearchActivity.class,
                        intent -> intent.putExtra("keyword", keyword));
            }
            else Toast.makeText(MainActivity.this,"搜索框不能为空",Toast.LENGTH_SHORT).show();
        });

        searchLine.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchLine.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    NavigationUtils.navigateWith(MainActivity.this, SearchActivity.class,
                            intent -> intent.putExtra("keyword", keyword));
                }
                else{
                    Toast.makeText(MainActivity.this,"搜索框不能为空",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

    }

    private void initViews() {
        navigationView = findViewById(R.id.main_bottom_nav);
        navigationView.setSelectedItemId(R.id.navigation_home);
        recyclerView = findViewById(R.id.news_recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsListAdapter = new NewsListAdapter(this);
        newsListAdapter.setPageType(NewsListAdapter.PAGE_TYPE_MAIN);
        recyclerView.setAdapter(newsListAdapter);
        searchImg = findViewById(R.id.main_img_search);
        searchLine = findViewById(R.id.search_edit_text);
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
                    newsListAdapter.setNewsList(newsList);
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