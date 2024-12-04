package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * è§†é¢‘è¯„è®ºè¡¨
 * @TableName comments
 */
@TableName(value ="comments")
@Data
public class Comments implements Serializable {
    /**
     * è¯„è®ºID
     */
    @TableId
    private String id;

    /**
     * çˆ¶è¯„è®ºID
     */
    private String fatherCommentId;

    /**
     * å›žå¤çš„ç›®æ ‡ç”¨æˆ·ID
     */
    private String toUserId;

    /**
     * è§†é¢‘ID
     */
    private String videoId;

    /**
     * è¯„è®ºè€…ID
     */
    private String fromUserId;

    /**
     * è¯„è®ºå†…å®¹
     */
    private String comment;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    private Date createTime;

    /**
     * æœ€åŽæ›´æ–°æ—¶é—´
     */
    private Date updateTime;

    /**
     * ç‚¹èµžæ•°é‡
     */
    private Integer likes;

    /**
     * å›žå¤æ•°é‡
     */
    private Integer repliesCount;

    /**
     * è¯„è®ºçŠ¶æ€
     */
    private Object status;

    /**
     * ä¸¾æŠ¥æ¬¡æ•°
     */
    private Integer reportCount;

    /**
     * æ˜¯å¦ç½®é¡¶
     */
    private Integer isPinned;

    /**
     * é¡¶çº§è¯„è®ºIDï¼ˆå¦‚æžœæ­¤è¯„è®ºæ˜¯å›žå¤ï¼Œåˆ™æŒ‡å‘é¡¶çº§è¯„è®ºçš„IDï¼‰
     */
    private String parentId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}