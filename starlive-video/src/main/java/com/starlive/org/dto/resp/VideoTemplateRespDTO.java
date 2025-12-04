package com.starlive.org.dto.resp;


import com.starlive.org.enity.TagTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;

@Data
@AllArgsConstructor
@Schema(description = "视频查询返回实体")
public class VideoTemplateRespDTO {
    /**
     * 视频Id
     */
    @Schema(description = "视频Id")
    private Long Id;
    /**
     * 视频题目
     */
    @Schema(description = "视频题目")
    private String title;
    /**
     * 视频简介
     */
    @Schema(description = "视频简介")
    private String description;
    /**
     * 视频作者
     */
    @Schema(description = "视频作者")
    private String author;
    /**
     * 视频地址
     */
    @Schema(description = "视频地址")
    private String url;
    /**
     * 视频标签
     */
    @Schema(description = "视频标签")
    private Set<TagTemplate> tags;
    /**
     * 视频状态
     */
    @Schema(description = "视频状态")
    private Integer status;

}
