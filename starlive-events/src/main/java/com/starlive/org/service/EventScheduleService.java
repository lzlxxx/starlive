package com.starlive.org.service;

import com.starlive.org.dto.EventScheduleDto;
import com.starlive.org.pojo.EventSchedule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
* @author nan
* @description 针对表【event_schedule】的数据库操作Service
* @createDate 2024-11-20 16:54:21
*/
public interface EventScheduleService extends IService<EventSchedule> {
    public void addEventSchedule(Long event_id,List<EventScheduleDto> eventScheduleDto) throws IOException;

}
