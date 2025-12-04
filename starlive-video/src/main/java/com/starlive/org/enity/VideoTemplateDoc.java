package com.starlive.org.enity;

import com.starlive.org.constant.VideoStatusEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

/**
 * 视频类文档实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "video_template")
public class VideoTemplateDoc {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 视频唯一标识符
     */
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    /**
     * 上传者ID
     */
    @Field(type = FieldType.Long)
    private Long uploaderId;
    /**
     * 作者名
     */
    @Field(type = FieldType.Text)
    private String author;
    /**
     * 上传视频标题
     */
    @Field(type = FieldType.Text)
    private String title;
    /**
     * 视频简介
     */
    @Field(type = FieldType.Text)
    private String description;
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, pattern = DATE_TIME_PATTERN , index = false)
    private Date createTime;
    /**
     * 上传时间，可以进行最近更新的筛选
     */
    @Field(type = FieldType.Date, pattern = DATE_TIME_PATTERN )
    private Date uploadTime;
    /**
     * 视频时长
     */
    @Field(type = FieldType.Date, pattern = DATE_TIME_PATTERN , index = false)
    private Integer duration;
    /**
     * 视频观看数
     */
    @Field(type = FieldType.Integer , index = false)
    private Integer views;
    /**
     * 视频点赞数
     */
    @Field(type = FieldType.Integer , index = false)
    private Integer likes;
    /**
     * 上传视频url
     */
    @Field(type = FieldType.Text)
    private String url;
    /**
     * 视频收藏数
     */
    @Field(type = FieldType.Integer , index = false)
    private Integer totalCollections;   // 视频累计收藏总数
    /**
     * 视频标签
     */
    @Field(type = FieldType.Nested)
    private Set<TagTemplate> tags;
    /**
     * 视频分类
     */
    @Field(type = FieldType.Text)
    private String category;
    /**
     * 视频状态
     */
    @Field(type = FieldType.Keyword)
    private VideoStatusEnums status;
    /**
     * 视频隐私设置
     */
    @Field(type = FieldType.Boolean , index = false)
    private Boolean privacy;
}
