package com.starlive.org.enity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Files {
    private Long id;
    private Long userId;
    private String uploadId;
    private String md5;
    private String url;
    private String bucket;
    private String object;
    private String originFileName;
    private Long size;
    private String type;
    private Long chunkSize;
    private Integer chunkCount;
    private String isDelete;
    private Timestamp createTime;
    private Timestamp updatedTime;
}

