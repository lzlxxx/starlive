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
 * @TableName event_schedule
 */
@TableName(value ="event_schedule")
@Data
public class EventSchedule implements Serializable {
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
     * æ—¥ç¨‹åç§°
     */

    private String scheduleName;

    /**
     * æ—¥ç¨‹æ—¶é—´
     */
    private Date scheduleTime;

    /**
     * æ—¥ç¨‹åœ°ç‚¹
     */
    private String location;

    /**
     * æ—¥ç¨‹æè¿°
     */
    private String description;

    /**
     * æ—¥ç¨‹åˆ›å»ºæ—¶é—´
     */
    private Date createTime;

    /**
     * æ—¥ç¨‹æ›´æ–°æ—¶é—´
     */
    private Date updateTime;

    /**
     * é€»è¾‘åˆ é™¤æ ‡å¿—ï¼Œ0ä¸ºæœªåˆ é™¤ï¼Œ1ä¸ºå·²åˆ é™¤
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}