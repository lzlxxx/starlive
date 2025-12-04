package com.starlive.org.mapper;

import com.starlive.org.pojo.LiveCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author nan
* @description 针对表【live_count】的数据库操作Mapper
* @createDate 2024-10-15 18:04:15
* @Entity com.starlive.org.Pojo.LiveCount
*/
@Mapper
public interface LiveCountMapper extends BaseMapper<LiveCount> {
    @Select("select  live_likes from live_count where id=#{Id}")
    public Long getLiveCount(@Param("Id") Long id);
    @Select("select id,room_id,user_id,max_people,live_likes,comments,click_commodity,click_activity,create_time,update_time,live_follows from live_count where id=#{Id}")
    public LiveCount getLiveCountById(@Param("Id") Long id);
    @Update("update live_count set live_likes=#{liveLikes} where id=#{Id}")
   public int updateLiveCount(@Param("Id") Long id, @Param("liveLikes") Long liveLikes);
}




