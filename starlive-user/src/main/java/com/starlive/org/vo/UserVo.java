package com.starlive.org.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String userName;
    private String ipAddress;
    private Long followedId;
    private String bio;


}
