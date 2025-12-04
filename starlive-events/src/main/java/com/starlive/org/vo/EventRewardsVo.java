package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRewardsVo {

    private Long rewardId;


    private String rewardName; // 奖励名称，全文检索


    private String rewardDescription; // 奖励描述，支持全文搜索


    private int rewardQuantity; // 奖励数量
}
