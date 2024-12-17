package com.example.lzz_finaltask.model;

import lombok.Data;

@Data
public class BanRecord {
    private Long id;
    private Long userId;
    private Long adminId;
    private Integer actionType; // 0:封禁 1:解封
    private String actionTime;
    private String reason;
    
    // 额外字段，用于显示
    private String username;    // 被封禁用户名
    private String adminName;   // 管理员名称
}