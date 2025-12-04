package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 视频信息表
 * </p>
 *
 * @author meng
 * @since 2024-10-29
 */
@ApiModel(value = "Video对象", description = "视频信息表")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("视频唯一标识符")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("上传者的用户ID")
    private Integer uploaderId;

    @ApiModelProperty("视频标题")
    private String title;

    @ApiModelProperty("视频描述")
    private String description;

    @ApiModelProperty("视频文件的存储路径")
    private String filePath;

    @ApiModelProperty("视频缩略图的存储路径")
    private String thumbnailPath;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("视频上传时间，默认为当前时间")
    private LocalDateTime uploadTime;

    @ApiModelProperty("视频时长（秒）")
    private Integer duration;

    @ApiModelProperty("视频观看次数")
    private Integer views;

    @ApiModelProperty("视频点赞次数")
    private Integer likes;

    @ApiModelProperty("视频评论次数")
    private Integer comments;

    @ApiModelProperty("视频标签，逗号分隔")
    private String tags;

    @ApiModelProperty("视频分类")
    private String category;

    @ApiModelProperty("视频状态，如待审核、已通过、已拒绝")
    private String status;

    @ApiModelProperty("视频隐私设置")
    private String privacy;

    @ApiModelProperty("页面访问量")
    private Integer pv;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    @Override
    public String toString() {
        return "Video{" +
            "id = " + id +
            ", uploaderId = " + uploaderId +
            ", title = " + title +
            ", description = " + description +
            ", filePath = " + filePath +
            ", thumbnailPath = " + thumbnailPath +
            ", createTime = " + createTime +
            ", uploadTime = " + uploadTime +
            ", duration = " + duration +
            ", views = " + views +
            ", likes = " + likes +
            ", comments = " + comments +
            ", tags = " + tags +
            ", category = " + category +
            ", status = " + status +
            ", privacy = " + privacy +
            ", pv = " + pv +
        "}";
    }
}
