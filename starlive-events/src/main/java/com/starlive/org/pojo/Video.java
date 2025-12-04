package com.starlive.org.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 视频信息表
 * </p>
 *
 * @author meng
 * @since 2024-10-29
 */
@Data
@ApiModel(value = "Video对象", description = "视频信息表")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("视频唯一标识符")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer videoId;

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
    private Date createTime;

    @ApiModelProperty("视频上传时间，默认为当前时间")
    private Date uploadTime;

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
    @ApiModelProperty("绑定活动Id")
    private Long eventId;


    @Override
    public String toString() {
        return "Video{" +
            "id = " + videoId +
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
