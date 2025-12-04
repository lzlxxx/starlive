package com.starlive.org.mapper;

import com.starlive.org.pojo.EventStatusLogs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
* @author nan
* @description 针对表【event_status_logs】的数据库操作Mapper
* @createDate 2024-11-09 00:39:29
* @Entity com.starlive.org.pojo.EventStatusLogs
*/
@Mapper
public interface EventStatusLogsMapper extends BaseMapper<EventStatusLogs> {
    @Insert("INSERT INTO event_status_logs (event_id, old_status, new_status, changed_by, changed_at, create_time) " +
            "VALUES (#{eventId}, #{oldStatus}, #{newStatus}, #{changedBy}, #{changedAt}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStatusLog(EventStatusLogs statusLog);
}




