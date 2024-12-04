package com.starlive.org.mapper;

import com.starlive.org.dto.EventRewardDto;
import com.starlive.org.pojo.EventRewards;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author nan
* @description 针对表【event_rewards】的数据库操作Mapper
* @createDate 2024-11-09 00:38:35
* @Entity com.starlive.org.pojo.EventRewards
*/
@Mapper
public interface EventRewardsMapper extends BaseMapper<EventRewards> {
    @Insert("INSERT INTO event_rewards (event_id, reward_name, description, quantity, create_time) " +
            "VALUES (#{eventId}, #{rewardName}, #{description}, #{quantity},CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReward(EventRewards reward);
}




