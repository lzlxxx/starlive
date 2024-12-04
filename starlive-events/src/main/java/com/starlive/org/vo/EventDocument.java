package com.starlive.org.vo;


import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.dto.EventScheduleDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(indexName = "events")
@Data
public class EventDocument {
    @Id
    private Long id; // 主键，映射为 Elasticsearch 的文档 ID
    @Field(type = FieldType.Long)
    private Long organizerId; // 活动组织者ID

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String title; // 活动名称，支持全文搜索

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String description; // 活动描述，支持全文搜索

    @Field(type = FieldType.Date)
    private Date startTime; // 活动开始时间

    @Field(type = FieldType.Date)
    private Date endTime; // 活动结束时间

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String location; // 活动地点

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String organizerName; // 组织者名称

    @Field(type = FieldType.Nested)
    private List<EventRewardsVo> rewards=new ArrayList<>(); // 奖励
    @Field(type = FieldType.Nested)
    private List<EventScheduleVo> schedule=new ArrayList<>(); // 活动日程

    @Field(type = FieldType.Keyword)
    private String status; // 活动状态

    @Field(type = FieldType.Integer)
    private Integer delFlag; // 逻辑删除标志

    @Field(type = FieldType.Text, store = true)
    private String posterUrl; // 活动海报URL
}
