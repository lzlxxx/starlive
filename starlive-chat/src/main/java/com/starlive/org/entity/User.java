package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
    private GeoPoint location;
    private String ipAddress;
    private Boolean isTest;
    private String bio;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String phone;
} 