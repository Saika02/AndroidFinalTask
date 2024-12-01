package com.example.lzz_finaltask.network.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseResponse {
    private int code;
    private Object data;
    private String message;
    private String description;
}

