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
 * @TableName live_count
 */
@TableName(value ="live_count")
@Data
public class LiveCount implements Serializable {
    /**
     * ä¸»é”®id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * ç›´æ’­é—´id
     */

    @TableField("room_id")
    private Long roomId;

    /**
     * ä¸»æ’­id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * è§‚çœ‹äººæ•°å³°å€¼
     */
    @TableField("max_people")
    private Long maxPeople;

    /**
     * ç›´æ’­é—´ç‚¹èµžæ€»æ•°
     */
    @TableField("live_likes")
    private Long liveLikes;

    /**
     * å¼¹å¹•æ€»æ•°
     */
    private Long comments;

    /**
     * ç‚¹å‡»å•†å“é“¾æŽ¥æ€»æ•°
     */
    @TableField("click_commodity")
    private Long clickCommodity;

    /**
     * ç‚¹å‡»æ´»åŠ¨é“¾æŽ¥æ€»æ•°
     */
    @TableField("click_activity")
    private Long clickActivity;

    /**
     * å¼€æ’­æ—¶é—´
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * ä¸‹æ’­æ—¶é—´
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 
     */
    @TableField("live_follows")
    private Long liveFollows;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}