package com.starlive.org.controller;

import com.starlive.org.dto.CommentsRequest;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.CommentsService;
import com.starlive.org.vo.CommentsResult;
import com.starlive.org.vo.ReplyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Validated
public class CommentsController {
    @Autowired
    private CommentsService commentsService;
    @PostMapping("/addComment")//增加评论
    public WebResult<Void> addComment(@RequestBody CommentsRequest commentsRequest) {
         try {
             commentsService.insertComment(commentsRequest);
         }catch (ServiceException e){
             return WebResultUtil.failure(e);
         }
        return WebResultUtil.success();
    }
    @PostMapping("/addReply")//回复评论
    public WebResult<Void> addReply(@RequestBody CommentsRequest commentsRequest) {
        try{
            commentsService.insertComment(commentsRequest);
        }catch (ServiceException e){
            return WebResultUtil.failure(e);
        }
        return WebResultUtil.success();
}
   @PostMapping("/removeComment")//删除评论
    public WebResult<Void> removeComment(@RequestParam("id")String id, @RequestParam("userId")String  userId) {
        try{
            commentsService.removeComment(id,userId);
        }catch (ServiceException e){
            return WebResultUtil.failure(e);
        }
       return WebResultUtil.success();
    }
    @PostMapping("/updateLikesCount")//增加或减少点赞数    减少时num只能为1
    public WebResult<Void> updateRepliesCount(@RequestParam("id")String id,@RequestParam("num") Integer num,@RequestParam("type") String type) {
        try{
            commentsService.updateLikeCount(id,num,type);
        }catch (ServiceException e){
            return WebResultUtil.failure(e);
        }
        return WebResultUtil.success();
    }
    @GetMapping("/{videoId}")//获取顶级评论
    public WebResult<List<CommentsResult>> getComments(@PathVariable String videoId,@RequestParam("limit") int limit,@RequestParam("offset") int offset) {
        List<CommentsResult> comments=null;
        try{
             comments = commentsService.getComments(videoId,limit,offset);
        }catch (ServiceException e){
            return WebResultUtil.failure(e);
        }
        return WebResultUtil.success(comments);
    }
    @GetMapping("/{videoId}/{commentId}")//获取回复
    public WebResult<List<ReplyResult>> getReplies(@PathVariable String videoId, @PathVariable String commentId, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        List<ReplyResult> replies=null;
        try{
            replies = commentsService.getReplies(videoId,commentId,limit,offset);
        }catch (ServiceException e){
            return WebResultUtil.failure(e);
        }
        return WebResultUtil.success(replies);

    }
    @GetMapping("/{videoId}/getComments")//获取评论区评论数
    public WebResult<Integer>getCommentsCount(@PathVariable String videoId) {
        return   WebResultUtil.success( commentsService.getCommentsCount(videoId));
    }
}
