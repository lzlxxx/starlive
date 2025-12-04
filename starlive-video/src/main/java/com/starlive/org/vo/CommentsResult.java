package com.starlive.org.vo;

import com.starlive.org.pojo.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsResult {
    private String commentId;//评论的id
    private String fatherCommentId;//回复评论的父评论的id   为null的话就是顶评论
    private String fromUserId;//评论的用户id
    private String  username;
    private String  avatarUrl;
    private GeoPoint userLocation;
    private String comment;//评论内容
    private Integer likeCount;//点赞数
    private Integer repliesCount;//回复数
    private Integer isPinned;//是否置顶
}
