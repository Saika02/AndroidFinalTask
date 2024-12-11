package com.example.lzz_finaltask.model;

import lombok.Data;

@Data
public class NewsType {
    private String type;
    private String typeDesc;
    private int iconResId; // 改用本地资源ID
}