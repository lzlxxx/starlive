package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("user_data")
@ApiModel(value = "UserData对象", description = "")
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId("user_id")
    private Long userId;

    @ApiModelProperty("使用JSON格式存储用户动态数据")
    private String data;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId = " + userId +
                ", data = " + data +
                "}";
    }
}
