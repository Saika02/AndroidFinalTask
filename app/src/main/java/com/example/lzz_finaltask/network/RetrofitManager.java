package com.example.lzz_finaltask.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String BASE_URL = "http://10.0.2.2:8080/android/";  // 你的实际 BASE_URL
    private static final ApiService apiService;

    static {
        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 创建 API 服务接口实例
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}