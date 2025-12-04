package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName users
 */
@TableName(value ="users")
@Data

public class Users implements Serializable {
    /**
     * ç”¨æˆ·å”¯ä¸€ID
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * ç”¨æˆ·å
     */
    @TableField("username")
    private String username;

    /**
     * å¯†ç ï¼ˆéœ€åŠ å¯†å¤„ç†ï¼‰
     */
    private String password;

    /**
     * é‚®ç®±
     */
    private String email;

    /**
     * å¤´åƒçš„URLåœ°å€
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * åœ°ç†ä½ç½®ä¿¡æ¯ï¼ˆç»çº¬åº¦ï¼‰
     */
    private GeoPoint location;

    /**
     * ç”¨æˆ·IPåœ°å€ï¼ˆIPv6å…¼å®¹ï¼‰
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * æ˜¯å¦æ˜¯ç®¡ç†å‘˜ï¼ˆæµ‹è¯•ï¼‰è´¦æˆ·
     */
    @TableField("is_test")
    private Integer isTest;

    /**
     * ç”¨æˆ·ç®€ä»‹
     */
    private String bio;

    /**
     * æ³¨å†Œæ—¶é—´
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * æ›´æ–°æ—¶é—´
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 
     */
    @TableField("followers_count")
    private Long followersCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}