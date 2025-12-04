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
 * @TableName event_rewards
 */
@TableName(value ="event_rewards")
@Data
public class EventRewards implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * æ´»åŠ¨ID
     */
    private Long eventId;

    /**
     * å¥–åŠ±åç§°
     */
    private String rewardName;

    /**
     * å¥–åŠ±æè¿°
     */
    private String description;

    /**
     * å¥–åŠ±æ•°é‡
     */
    private Integer quantity;

    /**
     * æ˜¯å¦å·²å‘æ”¾
     */
    private Boolean distributed;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    private Date createTime;

    /**
     * æ›´æ–°æ—¶é—´
     */
    private Date updateTime;

    /**
     * é€»è¾‘åˆ é™¤æ ‡å¿—ï¼Œ0ä¸ºæœªåˆ é™¤ï¼Œ1ä¸ºå·²åˆ é™¤
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}