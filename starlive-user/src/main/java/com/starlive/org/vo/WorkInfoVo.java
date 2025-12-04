package com.starlive.org.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class WorkInfoVo implements Serializable {
    private static final long serialVersionUID = 1234998L;

    private String file_path;
    private String thumbnail_path;
    private int likes;
    private String tags;

}
