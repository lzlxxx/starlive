package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 *
 * </p>
 *
 * @author meng
 * @since 2024-10-11
 */
@ApiModel(value = "Followers对象", description = "")
public class Followers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关注者ID")
    //@MppMultiId("follower_id")
    @TableId(value = "follower_id", type = IdType.INPUT)
    private Long followerId;

    @ApiModelProperty("被关注者ID")
    //@MppMultiId("followed_id")
    @TableField(value = "followed_id", insertStrategy = FieldStrategy.IGNORED)
    private Long followedId;

    @ApiModelProperty("关注时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowedId() {
        return followedId;
    }

    public void setFollowedId(Long followedId) {
        this.followedId = followedId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Followers{" +
                "followerId = " + followerId +
                ", followedId = " + followedId +
                ", createTime = " + createTime +
                ", updateTime = " + updateTime +
                "}";
    }
}
