package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.FutureOrPresent;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRewardsVo {
    @Field(type = FieldType.Keyword, store = true)
    private Long rewardId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String rewardName; // 奖励名称，全文检索

    @Field(type = FieldType.Text, store = true)
    private String rewardDescription; // 奖励描述，支持全文搜索

    @Field(type = FieldType.Integer)
    private int rewardQuantity; // 奖励数量
}
