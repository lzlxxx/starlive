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
@ApiModel(value = "Companion对象", description = "")
public class Companion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    //@MppMultiId("user_id")
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty("搭子ID")
    //@MppMultiId("companion_id")
    @TableField(value = "companion_id", insertStrategy = FieldStrategy.IGNORED)
    private Long companionId;

    @ApiModelProperty("搭子关系创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("搭子关系结束时间")
    private LocalDateTime endTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanionId() {
        return companionId;
    }

    public void setCompanionId(Long companionId) {
        this.companionId = companionId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Companion{" +
                "userId = " + userId +
                ", companionId = " + companionId +
                ", createTime = " + createTime +
                ", endTime = " + endTime +
                "}";
    }
}
