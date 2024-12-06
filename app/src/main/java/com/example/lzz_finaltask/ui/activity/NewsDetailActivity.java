package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsDetailActivity extends AppCompatActivity {
    private WebView webView;
    private TextView titleTextView;
    private TextView dateTextView;
    private ProgressBar progressBar;
    private static final String TAG = "NewsDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_info);
        initViews();
        setupWebView();
        loadNewsContent();
    }

    private void initViews() {
        webView = findViewById(R.id.news_webview);
        titleTextView = findViewById(R.id.news_title);
        dateTextView = findViewById(R.id.news_date);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadNewsContent() {
        String newsUrl = getIntent().getStringExtra("news_url");
        String newsTitle = getIntent().getStringExtra("news_title");  // 添加这行
        String newsDate = getIntent().getStringExtra("news_date");    // 添加这行

        titleTextView.setText(newsTitle);
        dateTextView.setText(newsDate);
        progressBar.setVisibility(View.VISIBLE);  // 显示进度条

        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsDetail(newsUrl);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse body = response.body();
                if (body.getCode() == 0) {
                    String htmlContent = (String) body.getData();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        webView.loadDataWithBaseURL(newsUrl, htmlContent,
                                "text/html", "utf-8", null);
                    });
                } else {
                    handleError("服务器响应错误");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError("网络请求失败");
            }
        });

    }

    private void handleError(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);

        // 设置WebView客户端
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
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
}