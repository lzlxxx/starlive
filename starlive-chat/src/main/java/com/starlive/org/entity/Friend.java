package com.starlive.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.KeySequence;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friends")
@KeySequence(value = "user_id, friend_id")
public class Friend {

    private Long userId;

    private Long friendId;

    private LocalDateTime createTime;

} 