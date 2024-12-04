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
 * @TableName event_status_logs
 */
@TableName(value ="event_status_logs")
@Data
public class EventStatusLogs implements Serializable {
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
     * åŽŸçŠ¶æ€
     */
    private Object oldStatus;

    /**
     * æ–°çŠ¶æ€
     */
    private Object newStatus;

    /**
     * çŠ¶æ€æ”¹å˜æ—¶é—´
     */
    private Date changedAt;

    /**
     * çŠ¶æ€ä¿®æ”¹è€…ID
     */
    private Long changedBy;

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