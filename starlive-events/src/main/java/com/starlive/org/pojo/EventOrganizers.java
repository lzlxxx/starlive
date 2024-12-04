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
 * @TableName event_organizers
 */
@TableName(value ="event_organizers")
@Data
public class EventOrganizers implements Serializable {
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
     * ç»„ç»‡è€…ç”¨æˆ·ID
     */
    private Long organizerId;

    private String username;
    /**
     * ç»„ç»‡è€…è§’è‰²
     */
    private Object role;

    /**
     * åŠ å…¥æ—¶é—´
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