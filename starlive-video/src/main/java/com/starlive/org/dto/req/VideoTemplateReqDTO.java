package com.starlive.org.dto.req;

import com.starlive.org.enity.TagTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "视频查询请求实体")
public class VideoTemplateReqDTO {
    /**
     * 视频Id
     */
    @Schema(description = "视频Id")
    private Long Id;
    /**
     * 视频名
     */
    @Schema(description = "视频名")
    private String title;
    /**
     * 视频作者
     */
    @Schema(description = "视频作者")
    private String author;
    /**
     * 视频标签
     */
    @Schema(description = "视频标签")
    private Set<TagTemplate> tags;
}
