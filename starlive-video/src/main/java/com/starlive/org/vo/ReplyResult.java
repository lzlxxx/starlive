package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResult {
    private String commentId;//评论id
    private String username;
    private String avatarUrl;
    private String userLocation;
    private String fatherCommentId;//父评论id
    private String toUserId;//被回复的用户id
    private String fromUserId;//回复者的id
    private String comment;//回复内容
    private Integer likes;//点赞数
    private Integer repliesCount;//回复数
}
