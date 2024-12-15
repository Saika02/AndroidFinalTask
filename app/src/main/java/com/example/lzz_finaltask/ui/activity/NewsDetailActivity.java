package com.example.lzz_finaltask.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.Comment;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.ApiService;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.CommentAdapter;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private RecyclerView commentList;
    private CommentAdapter commentAdapter;
    private Button btnSendComment;
    private EditText commentLine;
    private View emptyView;

    //TODO 评论区实现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_info);
        initViews();
        setClickListners();
        setupWebView();
        loadNewsContent();
        initCommentSection();
        addToHistory();
    }


    private void initViews() {
        webView = findViewById(R.id.news_webview);
        titleTextView = findViewById(R.id.news_title);
        dateTextView = findViewById(R.id.news_date);
        progressBar = findViewById(R.id.progress_bar);
        btnFavorite = findViewById(R.id.btn_favorite);
        commentList = findViewById(R.id.comment_list);
        commentLine = findViewById(R.id.comment_input);
        btnSendComment = findViewById(R.id.btn_send_comment);
        emptyView = findViewById(R.id.empty_comment_view);
        user = SharedPreferencesUtil.getUser(this);
        checkIsFavorite();
    }

    private void initCommentSection() {
        View emptyView = findViewById(R.id.empty_comment_view);
        Long newsId = getIntent().getLongExtra("news_id", 0);
        // 设置RecyclerView
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        commentList.setAdapter(commentAdapter);

        // 加载评论
        loadComments(newsId);
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
                    updateFavoriteIcon();
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

        btnSendComment.setOnClickListener(v -> {
            String commentContent = commentLine.getText().toString().trim();
            if (!TextUtils.isEmpty(commentContent)) {
                Long newsId = getIntent().getLongExtra("news_id", 0);
                User user = SharedPreferencesUtil.getUser(NewsDetailActivity.this);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentTime = dateFormat.format(new Date());
                Comment temp = new Comment(user.getUserId(), newsId, user.getUsername(), user.getAvatarUrl(), commentContent, currentTime);
                sendCommentInfo(temp);

            } else {
                handleError("评论内容不能为空");
            }
        });

        commentLine.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String content = commentLine.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    Long newsId = getIntent().getLongExtra("news_id", 0);
                    User user = SharedPreferencesUtil.getUser(NewsDetailActivity.this);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = dateFormat.format(new Date());
                    Comment temp = new Comment(user.getUserId(), newsId, user.getUsername(), user.getAvatarUrl(), content, currentTime);
                    sendCommentInfo(temp);
                    initCommentSection();
                } else {
                    handleError("评论内容不能为空");
                }
                return true;
            }
            return false;
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
        Long newsId = getIntent().getLongExtra("news_id", 0);
        Call<BaseResponse> call = RetrofitManager.getApiService().addHistory(userId, newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() != 0) handleError(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError(t.getMessage());
            }
        });
    }

    private void sendCommentInfo(Comment comment) {
        Call<BaseResponse> call = RetrofitManager.getApiService().sendComment(comment);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    // 评论成功
                    Toast.makeText(NewsDetailActivity.this,
                            "评论成功", Toast.LENGTH_SHORT).show();
                    // 清空输入框
                    commentLine.setText("");
                    // 隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentLine.getWindowToken(), 0);

                    loadComments(getIntent().getLongExtra("news_id",0));

                } else {
                    Toast.makeText(NewsDetailActivity.this,
                            "评论失败：" + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                handleError("发送评论失败");
            }
        });
    }


    private void loadComments(Long newsId) {
        Call<BaseResponse> call = RetrofitManager.getApiService().getNewsComment(newsId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    // 解析评论数据
                    List<Comment> comments = GsonUtil.parseList(response.body().getData(), Comment.class);
                    checkCommentListStatus(comments);
                    // 更新UI
                    updateCommentList(comments);

                } else {
                    // 处理错误
                    Toast.makeText(NewsDetailActivity.this,
                            "获取评论失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(NewsDetailActivity.this,
                        "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCommentListStatus(List<Comment> comments) {
        if (comments != null && !comments.isEmpty()) {
            // 有评论时显示列表
            emptyView.setVisibility(View.GONE);
            commentList.setVisibility(View.VISIBLE);
            commentAdapter.setComments(comments);
        } else {
            // 无评论时显示空态
            emptyView.setVisibility(View.VISIBLE);
            commentList.setVisibility(View.GONE);
        }

    }

    private void updateCommentList(List<Comment> comments) {

        if (comments != null && !comments.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            commentList.setVisibility(View.VISIBLE);
            commentAdapter.setComments(comments);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            commentList.setVisibility(View.GONE);
        }
    }
}




