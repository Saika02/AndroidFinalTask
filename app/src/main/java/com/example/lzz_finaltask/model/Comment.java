package com.example.lzz_finaltask.model;


import lombok.Data;

@Data
public class Comment {
    private Long id;          // 评论ID
    private Long newsId;      // 新闻ID
    private Long userId;      // 评论用户ID
    private String username;  // 用户名
    private String avatarUrl; // 用户头像URL
    private String content;   // 评论内容
    private String createTime; // 评论时间
    private Integer isDeleted;
    private Integer userRole;
//    private Integer likeCount; // 点赞数（可选）


    public Comment(Long userId, Long newsId, String username, String userAvatar, String content, String createTime,Integer userRole) {
        this.newsId = newsId;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = userAvatar;
        this.content = content;
        this.createTime = createTime;
        this.userRole = userRole;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", userId=" + userId +
                ", userName='" + username + '\'' +
                ", userAvatar='" + avatarUrl + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }

}
