package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName followers
 */
@TableName(value ="followers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableMPP
public class Followers implements Serializable {
    /**
     * 关注者ID
     */
    @TableId(value = "follower_id", type =IdType.INPUT)
//    @MppMultiId
    private Long followerId;

    /**
     * 被关注者ID
     */
    @TableField(value = "followed_id",insertStrategy = FieldStrategy.IGNORED)
//    @MppMultiId
    private Long followedId;

    /**
     * 关注状态，1表示关注，0表示取消关注
     */
    private Integer status;

    /**
     * 关注时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}