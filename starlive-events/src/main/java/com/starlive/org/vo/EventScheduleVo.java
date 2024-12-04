package com.starlive.org.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class   EventScheduleVo {
    @Field(type = FieldType.Keyword, store = true)
    private Long scheduleId;

    @NotBlank
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String scheduleName; // 日程名称，全文检索

    @NotNull
    @Field(type = FieldType.Date)
    private Date scheduleTime; // 日程时间，确保格式符合要求

    @NotBlank
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", store = true)
    private String scheduleLocation; // 地点，全文检索

    @Size(max = 100)
    @Field(type = FieldType.Text, store = true)
    private String scheduleDescription; // 描述
}
