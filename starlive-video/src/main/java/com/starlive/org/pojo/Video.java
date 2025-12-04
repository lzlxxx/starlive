package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频信息表
 * @TableName video
 */
@TableName(value ="video")
@Data
public class Video implements Serializable {
    /**
     * 视频唯一标识符
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 上传者的用户ID
     */
    private Integer uploaderId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频文件的存储路径
     */
    private String filePath;

    /**
     * 视频缩略图的存储路径
     */
    private String thumbnailPath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 视频上传时间，默认为当前时间
     */
    private Date uploadTime;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 视频观看次数
     */
    private Integer views;

    /**
     * 视频点赞次数
     */
    private Integer likes;

    /**
     * 视频累计收藏总数
     */
    private Integer totalCollections;

    /**
     * 视频评论次数
     */
    private Integer comments;

    /**
     * 视频标签，逗号分隔
     */
    private String tags;

    /**
     * 视频分类
     */
    private String category;

    /**
     * 视频状态，如待审核、已通过、已拒绝
     */
    private Object status;

    /**
     * 视频隐私设置
     */
    private Object privacy;

    /**
     * 页面访问量
     */
    private Integer pv;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}