package com.starlive.org.mapper;

import com.starlive.org.pojo.EventOrganizers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
* @author nan
* @description 针对表【event_organizers】的数据库操作Mapper
* @createDate 2024-11-09 00:39:46
* @Entity com.starlive.org.pojo.EventOrganizers
*/
@Mapper
public interface EventOrganizersMapper extends BaseMapper<EventOrganizers> {
    @Insert("INSERT INTO event_organizers (event_id, organizer_id, role, create_time) " +
            "VALUES (#{eventId}, #{organizerId}, #{role}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrganizer(EventOrganizers organizer);
}




