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
@ApiModel(value = "Friends对象", description = "")
public class Friends implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    //@MppMultiId("user_id")
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty("好友ID")
    //@MppMultiId("friend_id")
    @TableField(value = "friend_id", insertStrategy = FieldStrategy.IGNORED)
    private Long friendId;

    @ApiModelProperty("好友关系创建时间")
    private LocalDateTime createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "userId = " + userId +
                ", friendId = " + friendId +
                ", createTime = " + createTime +
                "}";
    }
}
