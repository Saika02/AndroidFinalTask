package com.example.lzz_finaltask.model;

import java.util.Date;

import lombok.Data;

@Data
public class News {
    private Long newsId;

    private String title;

    private String docurl;

    private String publishTime;

    private String imageUrl;

    private String newsType;

    private String typeDesc;

    private Date createTime;

    private Integer isDeleted;
}
