package com.example.lzz_finaltask.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lzz_finaltask.model.User;
import com.google.gson.Gson;

public class SharedPreferencesUtil {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";

    // 保存登录状态和用户信息
    public static void saveLoginState(Context context, boolean isLoggedIn, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // 将User对象转换为JSON字符串存储
        if (user != null) {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            editor.putString(KEY_USER, userJson);
        }
        editor.apply();
    }

    // 获取存储的用户信息
    public static User getUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // 检查是否已登录
    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 清除登录状态和用户信息
    public static void clearLoginState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // 更新用户信息
    public static void updateUser(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // 将User对象转换为JSON字符串存储
        if (user != null) {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            editor.putString(KEY_USER, userJson);
        }
        editor.apply();
    }
}