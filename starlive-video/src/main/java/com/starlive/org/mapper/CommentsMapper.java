package com.starlive.org.mapper;

import com.starlive.org.pojo.Comments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlive.org.vo.CommentsResult;
import com.starlive.org.vo.ReplyResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author nan
* @description 针对表【comments(è§†é¢‘è¯„è®ºè¡¨)】的数据库操作Mapper
* @createDate 2024-10-29 17:14:28
* @Entity com.starlive.org.pojo.Comments
*/
public interface CommentsMapper extends BaseMapper<Comments> {
    @Insert("INSERT INTO comments (" +
            "id, father_comment_id, to_user_id, video_id, from_user_id, " +
            "comment, create_time, update_time) " +
            "VALUES (" +
            "#{id}, #{fatherCommentId}, #{toUserId}, #{videoId}, #{fromUserId}, " +
            "#{comment}, #{createTime},#{updateTime} " +
            ")")
    public void insertComment(Comments comments);//增加一条评论或回复
    @Update("UPDATE comments SET replies_count = replies_count + 1 WHERE id = #{fatherCommentId}")
    public void addRepliesCount(@Param("fatherCommentId") String fatherCommentId);//增加回复数
    @Update("UPDATE comments SET replies_count = replies_count - 1 WHERE id = #{fatherCommentId}")
    public void reduceRepliesCount(@Param("fatherCommentId") String fatherCommentId);//减少回复数

    @Update("UPDATE comments SET likes = likes + #{num}  WHERE id = #{commentId}")
    public void addLikesCount(@Param("commentId") String commentId,@Param("num") int num);//增加点赞数
    @Update("UPDATE comments SET likes = likes - 1 WHERE id = #{commentId}")
    public void reduceLikesCount(@Param("commentId") String commentId);//减少点赞数
    @Delete("DELETE FROM comments WHERE id = #{commentId}")
//    @Delete("DELETE FROM comments WHERE id = #{commentId} or father_comment_id = #{commentId}")
    public void deleteComment(@Param("commentId") String commentId);//删除评论或回复  （及其子评论）
    @Select("Select id, father_comment_id, to_user_id, video_id, from_user_id,comment, create_time, update_time, likes, replies_count, status, report_count, is_pinned, parent_id FROM comments WHERE id = #{commentId}")
    public Comments getCommentById(String commentId);//根据评论id获取评论
    // 查询顶级评论 置顶评论优先
    @Select("SELECT " +
            "c.id AS comment_id, " +
            "c.father_comment_id AS father_comment_id, " +
            "c.from_user_id AS from_user_id, " +
            "u.username AS username, " +
            "u.avatar_url AS avatar_url, " +
            "u.location AS user_location, " +
            "c.comment AS comment, " +
            "c.likes AS like_count, " +
            "c.replies_count AS replies_count, " +
            "c.is_pinned AS is_pinned " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.from_user_id = u.user_id " +
            "WHERE c.video_id = #{videoId} AND c.father_comment_id IS NULL " +
            "ORDER BY c.is_pinned DESC, c.likes DESC, c.create_time DESC "+
            "LIMIT #{limit} OFFSET #{offset}")
    List<CommentsResult> getAllTopComments(@Param("videoId") String videoId, @Param("limit") int limit, @Param("offset") int offset);
    // 查询回复
    @Select("SELECT " +
            "r.id AS comment_id, " +
            "r.father_comment_id AS father_comment_id, " +
            "r.to_user_id AS to_user_id, " +
            "r.from_user_id AS from_user_id, " +
            "r.comment AS comment, " +
            "r.likes AS likes, " +
            "r.replies_count AS replies_count, " +
            "u.username AS username, " +
            "u.avatar_url AS avatar_url " +
            "FROM comments r " +
            "LEFT JOIN users u ON r.from_user_id = u.user_id " +
            "WHERE r.father_comment_id = #{commentId} " +
            "ORDER BY  r.create_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ReplyResult> getReplies(@Param("videoId") String videoId, @Param("commentId") String commentId, @Param("limit") int limit, @Param("offset") int offset);
    // 查询回复数
    @Select("SELECT COUNT(*) FROM comments WHERE video_id = #{videoId}")
    int getCommentsCount(@Param("videoId") String videoId);//获取评论区总数
    @Select("SELECT COUNT(*) FROM comments WHERE video_id = #{videoId} AND father_comment_id= #{commentId}")
    int getRepliesCount(@Param("videoId") String videoId,@Param("commentId") String commentId);//根据评论ID获取其回复数
}




