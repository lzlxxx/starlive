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
 * @TableName collection
 */
@TableName(value ="collection")
@Data
public class Collection implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long collectionId;

    /**
     * ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 
     */
    @TableField("content_type")
    private Object contentType;

    /**
     * ID
     */
    @TableField("folder_id")
    private Long folderId;

    /**
     * 
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}