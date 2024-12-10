package com.example.lzz_finaltask.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.ApiService;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsDetailActivity extends AppCompatActivity {
    private WebView webView;
    private TextView titleTextView;
    private TextView dateTextView;
    private ProgressBar progressBar;
    private static final String TAG = "NewsDetailActivity";
    private ImageButton btnFavorite;
    private boolean isFavorited = false;
    private User user;

    //TODO 评论区实现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_info);
        initViews();
        setClickListners();
        setupWebView();
        loadNewsContent();
        addToHistory();
    }




    private void initViews() {
        webView = findViewById(R.id.news_webview);
        titleTextView = findViewById(R.id.news_title);
        dateTextView = findViewById(R.id.news_date);
        progressBar = findViewById(R.id.progress_bar);
        btnFavorite = findViewById(R.id.btn_favorite);
        user = SharedPreferencesUtil.getUser(this);
        checkIsFavorite();
    }

    private void checkIsFavorite() {
        Long userId = user.getUserId();
        Long newsId = getIntent().getLongExtra("news_id", 0);
        Call<BaseResponse> call = RetrofitManager.getApiService().checkIsFavorite(userId, newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body.getCode() == 0) {
                    isFavorited = (Boolean) body.getData();
                    runOnUiThread(() -> updateFavoriteIcon());
                } else handleError(body.getMessage());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError(t.getMessage());
            }
        });
    }

    private void setClickListners() {
        btnFavorite.setOnClickListener(v -> {
            btnFavorite.setEnabled(false);
            performFavorite(isFavorited);
        });
    }

    private void performFavorite(boolean isFavorited) {
        ApiService apiService = RetrofitManager.getApiService();
        Long userId = user.getUserId();
        Long newsId = getIntent().getLongExtra("news_id", 0);

        Call<BaseResponse> call = isFavorited ?
                apiService.removeUserFavorite(userId, newsId) :
                apiService.addUserFavorite(userId, newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body.getCode() == 0) {
                    // 操作成功后再更新状态和UI
                    NewsDetailActivity.this.isFavorited = !isFavorited;
                    runOnUiThread(() -> {
                        updateFavoriteIcon();
                        btnFavorite.setEnabled(true);
                    });
                } else {
                    handleError(body.getMessage());
                    btnFavorite.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError(t.getMessage());
                btnFavorite.setEnabled(true);
            }
        });
    }

    private void loadNewsContent() {
        Long newsId = getIntent().getLongExtra("news_id", 0);
        String newsTitle = getIntent().getStringExtra("news_title");
        String newsDate = getIntent().getStringExtra("news_date");

        titleTextView.setText(newsTitle);
        dateTextView.setText(newsDate);
        progressBar.setVisibility(View.VISIBLE);

        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsContent(newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    handleError("服务器响应错误");
                    return;
                }
                BaseResponse body = response.body();
                if (body.getCode() == 0 && body.getData() != null) {
                    String htmlContent = (String) body.getData();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
                    });
                } else {
                    handleError("获取新闻内容失败");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError("网络请求失败");
            }
        });
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("UTF-8");

        // 设置WebView背景为白色
        webView.setBackgroundColor(Color.WHITE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                handleError("加载页面失败: " + description);
            }
        });
    }

    private void handleError(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private void updateFavoriteIcon() {
        btnFavorite.setImageResource(
                isFavorited ?
                        R.drawable.ic_favorite :      // 已收藏，显示实心
                        R.drawable.ic_favorite_border // 未收藏，显示空心
        );
    }

    private void addToHistory() {
        Long userId = user.getUserId();
        Long newsId = getIntent().getLongExtra("news_id",0);
        Call<BaseResponse> call = RetrofitManager.getApiService().addHistory(userId,newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.body().getCode()!=0) handleError(response.body().getMessage());
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError(t.getMessage());
            }
        });
    }
}




