package com.starlive.org.dto;

import com.starlive.org.pojo.EventRewards;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRewardDto {
    private String rewardName;
    private String rewardDescription;
    private int rewardQuantity;
}
