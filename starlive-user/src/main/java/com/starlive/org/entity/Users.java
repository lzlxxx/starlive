package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author meng
 * @since 2024-10-11
 */

@Data
@ApiModel(value = "Users对象", description = "")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户唯一ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码（需加密处理）")
    private String password;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("头像的URL地址")
    private String avatarUrl;

    @ApiModelProperty("地理位置信息（经纬度）")
    private GeoPoint location;

    @ApiModelProperty("用户IP地址（IPv6兼容）")
    private String ipAddress;

    @ApiModelProperty("是否是管理员（测试）账户")
    private Boolean isTest;

    @ApiModelProperty("用户简介")
    private String bio;

    @ApiModelProperty("注册时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;



    @Override
    public String toString() {
        return "Users{" +
                "userId = " + userId +
                ", username = " + username +
                ", password = " + password +
                ", email = " + email +
                ", avatarUrl = " + avatarUrl +
                ", location = " + location +
                ", ipAddress = " + ipAddress +
                ", isTest = " + isTest +
                ", bio = " + bio +
                ", createTime = " + createTime +
                ", updateTime = " + updateTime +
                "}";
    }



}
