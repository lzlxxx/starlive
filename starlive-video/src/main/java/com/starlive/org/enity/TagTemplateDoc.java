package com.starlive.org.enity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 标签索引
 */
@Data
@Document(indexName = "tag")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagTemplateDoc {
    /**
     * 对应标签的Id
     */
    @Field(type = FieldType.Long)
    private Long Id;
    /**
     * 对应标签的名称
     */
    @Field(type = FieldType.Text)
    private String tanName;
}
