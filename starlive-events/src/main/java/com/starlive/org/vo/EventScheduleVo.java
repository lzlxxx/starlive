package com.starlive.org.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class   EventScheduleVo {

    private Long scheduleId;

    @NotBlank
    private String scheduleName; // 日程名称，全文检索

    @NotNull
    private Date scheduleTime; // 日程时间，确保格式符合要求

    @NotBlank
    private String scheduleLocation; // 地点，全文检索

    @Size(max = 100)
    private String scheduleDescription; // 描述
}
