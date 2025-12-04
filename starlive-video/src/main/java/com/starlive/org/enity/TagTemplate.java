package com.starlive.org.enity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagTemplate {
    /**
     * 对应标签的Id
     */
    private Long Id;
    /**
     * 对应标签的名称
     */
    private String tagName;
}
