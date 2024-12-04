package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.dto.CommentsRequest;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.mapper.CommentsMapper;
import com.starlive.org.pojo.Comments;
import com.starlive.org.service.CommentsService;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.CommentsResult;
import com.starlive.org.vo.ReplyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author nan
* @description 针对表【comments(è§†é¢‘è¯„è®ºè¡¨)】的数据库操作Service实现
* @createDate 2024-10-29 17:14:28
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService{
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<CommentsResult> getComments(String videoId,int limit,int offset) {
        List<CommentsResult> comments=null;
        try {
            String key="firstComments:"+videoId;
            //1.分页查询redis中的一级评论
             comments = (List<CommentsResult>)(redisUtil.range(key, offset, offset + limit - 1));
             //2.如果redis中没有一级评论则从数据库中查询并存入redis且设置50秒的缓存时间
            if (comments == null || comments.isEmpty()) {
                comments=commentsMapper.getAllTopComments(videoId, limit, offset);
                if (comments != null && !comments.isEmpty()) {
                    redisUtil.rpushAllWithExpiry(key, comments, 50);
                }
            }
        }catch (Exception e){
            throw new ServiceException("获取评论失败");
        }
        return comments;
    }
    @Override

    public List<ReplyResult> getReplies(String videoId, String commentId, int limit, int offset) {
        List<ReplyResult> replies=null;
        try {
            String key="reply:"+videoId+":"+commentId;
            //1.分页查询redis中的一级评论
            replies = (List<ReplyResult>)(redisUtil.range(key, offset, offset + limit - 1));
            //2.如果redis中没有一级评论则从数据库中查询并存入redis且设置50秒的缓存时间
            if (replies == null || replies.isEmpty())
                replies=commentsMapper.getReplies(videoId,commentId, limit, offset);
                for (ReplyResult reply : replies) {
                    redisUtil.rpush(key, reply);
                    redisUtil.setExpire(key, 50, TimeUnit.SECONDS);
                }
                // 更新获取的回复列表
                replies =(List<ReplyResult>)redisUtil.range(key, offset, offset + limit - 1);

        }catch (Exception e){
            throw new ServiceException("获取回复失败");
        }
        return replies;
    }

    @Override
    public void insertComment(CommentsRequest commentsRequest){//插入评论或回复
        //1、获取被回复的评论的id也就是一级评论
        String fatherCommentId = commentsRequest.getFatherCommentId();
        try {
            Comments comments = getComments(commentsRequest);
            commentsMapper.insertComment(comments);
            //2.判断如果是回复评论的话也就是二级评论  则一级评论数+1
            if (fatherCommentId != null) {
                commentsMapper.addRepliesCount(fatherCommentId);
            }
        }catch(Exception e){
            throw new ServiceException("操作失败");
        }
    }
    @Override
    public void removeComment(String commentId,String userId) {//删除评论或回复
        //1、根据评论id获取评论信息
        Comments commentById = commentsMapper.getCommentById(commentId);
        if (commentById == null) {
            throw new ServiceException("评论不存在");
        }
        if (!commentById.getFromUserId().equals(userId)) {
            throw new ServiceException("无权限删除");
        }
        //2.删除评论
        commentsMapper.deleteComment(commentId);
        String fatherCommentId = commentById.getFatherCommentId();
        //3.如果该评论是回复评论的话 则一级评论数-1
        if (fatherCommentId != null) {
         commentsMapper.reduceRepliesCount(fatherCommentId);
        }
    }

    @Override
    public void updateLikeCount(String commentId, Integer num, String type) {
        try {
            //1，根据前端返回的类型判断是点赞还是取消点赞    取消点赞的话num只能为1
            switch (type) {
                case "1":
                    commentsMapper.addLikesCount(commentId, num);
                    break;
                case "0":
                    commentsMapper.reduceLikesCount(commentId);
                    break;
            }
        }catch (Exception e){
            throw new ServiceException("操作失败");
        }
    }

    @Override
    public Integer getCommentsCount(String videoId) {
        return commentsMapper.getCommentsCount(videoId);
    }

    private static Comments getComments(CommentsRequest commentsRequest) {
        //将请求参数转换为实体类
        Comments comments = new Comments();
        comments.setId(commentsRequest.getId());
        comments.setFatherCommentId(commentsRequest.getFatherCommentId());
        comments.setToUserId(commentsRequest.getToUserId());
        comments.setVideoId(commentsRequest.getVideoId());
        comments.setFromUserId(commentsRequest.getFromUserId());
        comments.setComment(commentsRequest.getComment());
        comments.setCreateTime(new Date());
        comments.setUpdateTime(new Date());
        return comments;
    }
}




