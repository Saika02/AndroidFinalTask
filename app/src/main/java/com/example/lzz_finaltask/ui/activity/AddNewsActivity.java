package com.example.lzz_finaltask.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.request.AddNewsRequest;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.ui.adapter.NewsTypeAdapter;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewsActivity extends AppCompatActivity {
    private EditText etTitle;
    private RichEditor editor;
    private AutoCompleteTextView spinnerType;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_news);




        initViews();
        setupEditor();
        setupSpinner();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        editor = findViewById(R.id.editor);
        spinnerType = findViewById(R.id.spinner_type);
        btnSubmit = findViewById(R.id.btn_publish);
        progressBar = findViewById(R.id.progress_bar);
        user = SharedPreferencesUtil.getUser(this);
        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitNews());
    }

    private void setupEditor() {
        editor = findViewById(R.id.editor);
        editor.setEditorHeight(300);
        editor.setEditorFontSize(16);
        editor.setPadding(16, 16, 16, 16);
        editor.setPlaceholder("在这里输入新闻内容...");
        editor.setBackgroundColor(Color.WHITE);
        editor.setEditorFontColor(Color.BLACK);
        editor.setInputEnabled(true);
    }

    private void setupSpinner() {
        // 使用你现有的新闻类型
        List<String> types = Arrays.asList(
                NewsTypeAdapter.DESC_YAOWEN,
                NewsTypeAdapter.DESC_GUONEI,
                NewsTypeAdapter.DESC_GUOJI,
                NewsTypeAdapter.DESC_WAR,
                NewsTypeAdapter.DESC_TECH,
                NewsTypeAdapter.DESC_MONEY,
                NewsTypeAdapter.DESC_SPORTS,
                NewsTypeAdapter.DESC_ENT
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void submitNews() {
        String title = etTitle.getText().toString().trim();
        String htmlContent = editor.getHtml(); // 获取HTML内容
        String type = spinnerType.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(htmlContent)) {
            Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(type)){
            Toast.makeText(this,"必须选择一个类型",Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);


        // 创建新闻对象
        AddNewsRequest addNewsRequest = new AddNewsRequest();
        addNewsRequest.setTitle(title);
        addNewsRequest.setNewsType(getNewsType(type));
        addNewsRequest.setPublishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date()));
        addNewsRequest.setHtmlContent(htmlContent);

        Call<BaseResponse> call = RetrofitManager.getApiService().addNews(addNewsRequest);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.body() != null && response.body().getCode() == 0) {
                    Toast.makeText(AddNewsActivity.this,
                            "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddNewsActivity.this,
                            "发布失败：" + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(AddNewsActivity.this,
                        "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getNewsType(String desc) {
        switch (desc) {
            case NewsTypeAdapter.DESC_YAOWEN: return NewsTypeAdapter.TYPE_YAOWEN;
            case NewsTypeAdapter.DESC_GUONEI: return NewsTypeAdapter.TYPE_GUONEI;
            case NewsTypeAdapter.DESC_GUOJI: return NewsTypeAdapter.TYPE_GUOJI;
            case NewsTypeAdapter.DESC_WAR: return NewsTypeAdapter.TYPE_WAR;
            case NewsTypeAdapter.DESC_TECH: return NewsTypeAdapter.TYPE_TECH;
            case NewsTypeAdapter.DESC_MONEY: return NewsTypeAdapter.TYPE_MONEY;
            case NewsTypeAdapter.DESC_SPORTS: return NewsTypeAdapter.TYPE_SPORTS;
            case NewsTypeAdapter.DESC_ENT: return NewsTypeAdapter.TYPE_ENT;
            default: return NewsTypeAdapter.TYPE_YAOWEN;
        }
    }
}