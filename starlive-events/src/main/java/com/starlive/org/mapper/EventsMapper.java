package com.starlive.org.mapper;

import com.starlive.org.pojo.Events;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectKey;

/**
* @author nan
* @description 针对表【events】的数据库操作Mapper
* @createDate 2024-11-09 00:36:04
* @Entity com.starlive.org.pojo.Events
*/
@Mapper
public interface EventsMapper extends BaseMapper<Events> {
    @Insert("INSERT INTO events (title, description, start_time, end_time, location, organizer_id, status, create_time, update_time, del_flag, poster_url) " +
            "VALUES (#{title}, #{description}, #{startTime}, #{endTime}, #{location}, #{organizerId}, #{status}, now(), now(), 0, #{posterUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertEvent(Events event);
}




