package com.starlive.org.model;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikePage {
    private Integer id;                // 视频唯一标识符
    private String filePath;           // 视频文件的存储路径
    private String thumbnailPath;      // 视频缩略图的存储路径
    private Integer likes;              // 视频点赞次数
}
