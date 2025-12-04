package com.starlive.org.enity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@TableName("likes")
public class Likes implements Serializable {
    @TableId("like_id")
    private Long likeId;         // 点赞记录的唯一标识符
    @TableField(value = "user_id")
    private Long userId;         // 用户ID
    @TableField(value = "content_id")
    private Long contentId;      // 视频ID
    @TableField(value = "status")
    private Integer status;       // 点赞状态，默认为1点赞
    @TableField(value = "create_time")
    private Timestamp createTime; // 点赞时间
}

