package com.starlive.org.mapper;

import com.starlive.org.pojo.EventSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author nan
* @description 针对表【event_schedule】的数据库操作Mapper
* @createDate 2024-11-20 16:54:21
* @Entity com.starlive.org.pojo.EventSchedule
*/
@Mapper
public interface EventScheduleMapper extends BaseMapper<EventSchedule> {
    @Insert("insert into event_schedule (event_id, schedule_name, schedule_time,location,description,create_time,update_time,del_flag) values (#{eventId}, #{scheduleName}, #{scheduleTime}, #{location},#{description},NOW(),NOW(),0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void insertSchedule(EventSchedule eventSchedule);
    @Select("select id,event_id,schedule_name,schedule_time,location,description,create_time,update_time,del_flag from event_schedule where event_id = #{eventId} and del_flag = 0")
    public List<EventSchedule> findByEventId(Long eventId);
    @Update("update event_schedule set schedule_name = #{scheduleName}, schedule_time = #{scheduleTime}, location = #{location}, description = #{description}, update_time = NOW() where id = #{id} and del_flag = 0")
    public int updateById(EventSchedule eventSchedule);
}






