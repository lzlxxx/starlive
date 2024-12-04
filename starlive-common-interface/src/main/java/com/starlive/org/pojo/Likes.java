package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName likes
 */
@TableName(value ="likes")
@Data//点赞表
public class Likes implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long likeId;

    /**
     * ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 1
     */
    private Integer status;

    /**
     *
     */
    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}