package com.starlive.org.enity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Video implements Serializable {
    private Integer id;                // 视频唯一标识符
    private Integer uploaderId;        // 上传者的用户ID
    private String title;              // 视频标题
    private String description;        // 视频描述
    private String filePath;           // 视频文件的存储路径
    private String thumbnailPath;      // 视频缩略图的存储路径
    private Date createTime;           // 创建时间
    private Date uploadTime;           // 视频上传时间
    private Integer duration;           // 视频时长（秒）
    private Integer views;              // 视频观看次数
    private Integer likes;              // 视频点赞次数
    private Integer totalCollections;   // 视频累计收藏总数
    private Integer comments;           // 视频评论次数
    private String tags;                // 视频标签，逗号分隔
    private String category;            // 视频分类
    private String status;              // 视频状态，如待审核、已通过、已拒绝
    private String privacy;             // 视频隐私设置
    private Integer pv;                 // 页面访问量
}
