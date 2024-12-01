package com.example.lzz_finaltask.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class GsonUtil {
    private static final Gson gson = new Gson();

    public static <T> T trans(LinkedTreeMap<String, Object> map, Class<T> clazz) {
        try {
            // 先将 LinkedTreeMap 转为 JSON 字符串
            String jsonStr = gson.toJson(map);
            // 再将 JSON 字符串转换为目标类型对象
            return gson.fromJson(jsonStr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}