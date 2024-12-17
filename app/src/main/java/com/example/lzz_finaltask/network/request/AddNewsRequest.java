package com.example.lzz_finaltask.network.request;

import lombok.Data;

@Data
public class AddNewsRequest {
    private String title;
    private String newsType;
    private String htmlContent;
    private String publishTime;
}
