package com.starlive.org.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
public class  EventRequest {
    @NotBlank
    private String organizerName;//组织者的用户名
    @NotNull
    private String title;
    @Size(max=100)
    private String description;
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @NotBlank
    private String location;
    @NotNull
    private Long organizerId;
    private String posterUrl;
    @PositiveOrZero
    private List<EventRewardDto> rewards;
}
