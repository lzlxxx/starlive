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
 * @TableName collection_folder
 */
@TableName(value ="collection_folder")
@Data
public class CollectionFolder implements Serializable {
    /**
     * 收藏夹的唯一标识符
     */
    @TableId(type = IdType.AUTO)
    private Long folderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏夹中收藏内容的数量
     */
    private Integer collectionCount;

    /**
     * 收藏夹名称
     */
    private String folderName;

    /**
     * 收藏夹类型，video 表示仅存视频，live 表示仅存直播
     */
    private Object folderType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}