package com.starlive.org.pojo;


import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

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
    @Id
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

    private GeoLocation locationPoint;

    /**
     * æ´»åŠ¨ç»„ç»‡è€…ID
     */
    private Long organizerId;

    /**
     * æ´»åŠ¨çŠ¶æ€
     */
    private Object status;

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
    public void setLocationPoint(double latitude, double longitude) {
        this.locationPoint = GeoLocation.of(b -> b
                .coords(List.of(latitude,longitude)));
    }

    public Double getLat() {
        if (locationPoint != null && locationPoint.isCoords()) {
            List<Double> coords = locationPoint.coords();
            return coords != null && coords.size() > 1 ? coords.get(1) : null;
        }
        return null;
    }

    public Double getLon() {
        if (locationPoint != null && locationPoint.isCoords()) {
            List<Double> coords = locationPoint.coords();
            return coords != null && !coords.isEmpty() ? coords.get(0) : null;
        }
        return null;
    }
}