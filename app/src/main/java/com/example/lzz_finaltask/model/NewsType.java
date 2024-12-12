package com.example.lzz_finaltask.model;

import lombok.Data;

@Data
public class NewsType {
    private String type;
    private String typeDesc;
    private int iconResId;

    public NewsType(String type, String typeDesc, int iconResId) {
        this.type = type;
        this.typeDesc = typeDesc;
        this.iconResId = iconResId;
    }
}