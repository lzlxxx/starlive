package com.starlive.org.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadInfo implements Serializable {

    @NotBlank(message = "md5不能为空")
    private String md5;

    @NotNull(message = "视频上传者id不能为空")
    private Long userId;

    private String uploadId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    private String url;

    private String object;
    private String type;

    @NotNull(message = "文件大小不能为空")
    private Long size;

    @NotNull(message = "分片数量不能为空")
    private Integer chunkCount;

    @NotNull(message = "分片大小不能为空")
    private Long chunkSize;

    private String contentType;

    // listParts 从 1 开始，前端需要上传的分片索引+1
    private List<Integer> listParts;

}
