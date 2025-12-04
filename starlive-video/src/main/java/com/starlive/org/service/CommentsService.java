package com.starlive.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starlive.org.dto.CommentsRequest;
import com.starlive.org.pojo.Comments;
import com.starlive.org.vo.CommentsResult;
import com.starlive.org.vo.ReplyResult;

import java.util.List;

/**
* @author nan
* @description 针对表【comments(è§†é¢‘è¯„è®ºè¡¨)】的数据库操作Service
* @createDate 2024-10-29 17:14:28
*/
public interface CommentsService extends IService<Comments> {
    //插入评论或回复
    public void insertComment(CommentsRequest commentsRequest);
    //删除评论或回复
    public void removeComment(String commentId,String userId);
    //更新点赞数
    public void updateLikeCount(String commentId,Integer num,String userId);
    //获取评论列表
    public List<CommentsResult> getComments(String videoId,int limit,int offset);
    //获取评论区评论总数
    public Integer getCommentsCount(String videoId);
    //获取回复列表
    public List<ReplyResult> getReplies(String video, String commentId, int limit, int offset);

}
