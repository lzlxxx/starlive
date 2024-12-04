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
 * @TableName events
 */
@TableName(value ="events")
@Data
public class Events implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * æ´»åŠ¨åç§°
     */
    private String title;

    /**
     * æ´»åŠ¨æè¿°
     */
    private String description;

    /**
     * æ´»åŠ¨å¼€å§‹æ—¶é—´
     */
    private Date startTime;

    /**
     * æ´»åŠ¨ç»“æŸæ—¶é—´
     */
    private Date endTime;

    /**
     * æ´»åŠ¨åœ°ç‚¹
     */
    private String location;

    /**
     * æ´»åŠ¨ç»„ç»‡è€…ID
     */
    private Long organizerId;

    /**
     * æ´»åŠ¨çŠ¶æ€
     */
    private String status;

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

    /**
     * 活动海报URL
     */
    private String posterUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}