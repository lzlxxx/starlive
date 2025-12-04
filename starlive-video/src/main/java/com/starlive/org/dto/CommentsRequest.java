package com.starlive.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsRequest {
    private String id;//评论的id
    private String fatherCommentId;//回复的父评论的id   为null的话就是顶评论
    private String toUserId;//被回复的用户的id
    private String videoId;//视频id
    private String fromUserId;//评论的用户id
    private String comment;//评论内容
}
