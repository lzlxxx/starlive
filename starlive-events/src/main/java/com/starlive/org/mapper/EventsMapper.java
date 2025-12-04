package com.starlive.org.mapper;

import com.starlive.org.handler.GeoPointTypeHandler;
import com.starlive.org.pojo.Events;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
* @author nan
* @description 针对表【events】的数据库操作Mapper
* @createDate 2024-11-09 00:36:04
* @Entity com.starlive.org.pojo.Events
*/
@Mapper
public interface EventsMapper extends BaseMapper<Events> {
    @Insert("INSERT INTO events (title, description, start_time, end_time, location, " +
            "location_point, organizer_id, status, create_time, update_time, del_flag, poster_url) " +
            "VALUES (#{title}, #{description}, #{startTime}, #{endTime}, #{location}, " +
            "ST_GeomFromText(#{locationPoint,typeHandler=com.starlive.org.handler.GeoPointTypeHandler}, 4326), " +
            "#{organizerId}, #{status}, NOW(), NOW(), 0, #{posterUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertEvent(Events event);



    @Select("SELECT id, title, description, start_time, end_time, location, " +
            "ST_AsText(location_point) as location_point, " +  
            "organizer_id, status, create_time, update_time " +
            "FROM events WHERE del_flag = 0 and id = #{eventId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "location", column = "location"),
            @Result(property = "locationPoint",
                    column = "location_point",
                    typeHandler = GeoPointTypeHandler.class),
            @Result(property = "organizerId", column = "organizer_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    Events selectEvent(@Param("eventId") Long eventId);
}




