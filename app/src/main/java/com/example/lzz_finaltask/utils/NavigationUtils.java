package com.example.lzz_finaltask.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

// utils/NavigationUtils.java
public class NavigationUtils {
    /**
     * 普通跳转
     */
    public static void navigateTo(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        context.startActivity(intent);
    }

    /**
     * 跳转并关闭当前页面
     */
    public static void navigateToAndFinish(Activity activity, Class<?> targetActivity) {
        Intent intent = new Intent(activity, targetActivity);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 带参数的跳转
     */
    public static void navigateWith(Context context, Class<?> targetActivity, 
                                  NavigationCallback callback) {
        Intent intent = new Intent(context, targetActivity);
        if (callback != null) {
            callback.onCallback(intent);
        }
        context.startActivity(intent);
    }

    /**
     * 清空任务栈并跳转
     */
    public static void navigateWithClearTask(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 参数回调接口
     */
    public interface NavigationCallback {
        void onCallback(Intent intent);
    }
}