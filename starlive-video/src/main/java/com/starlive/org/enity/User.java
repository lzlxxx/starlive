package com.starlive.org.enity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@TableName("users")
public class User implements Serializable {

    @TableId(value = "user_id") // 主键映射
    private Long userId; // 用户唯一ID

    @TableField(value = "username") // 映射到数据库表字段
    private String username; // 用户名

    @TableField(value = "password") // 映射到数据库表字段
    private String password; // 密码（需加密处理）

    @TableField(value = "email") // 映射到数据库表字段
    private String email; // 邮箱

    @TableField(value = "avatar_url") // 映射到数据库表字段
    private String avatarUrl; // 头像的URL地址

    private GeoPoint location;

    @TableField(value = "ip_address") // 映射到数据库表字段
    private String ipAddress; // 用户IP地址（IPv6兼容）

    @TableField(value = "is_test") // 映射到数据库表字段
    private Boolean isTest; // 是否是管理员（测试）账户

    @TableField(value = "bio") // 映射到数据库表字段
    private String bio; // 用户简介

    @TableField(value = "create_time") // 映射到数据库表字段
    private Timestamp createTime; // 注册时间

    @TableField(value = "update_time") // 映射到数据库表字段
    private Timestamp updateTime; // 更新时间
}

