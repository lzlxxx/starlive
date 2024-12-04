package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.pojo.EventStatusLogs;
import com.starlive.org.service.EventStatusLogsService;
import com.starlive.org.mapper.EventStatusLogsMapper;
import org.springframework.stereotype.Service;

/**
* @author nan
* @description 针对表【event_status_logs】的数据库操作Service实现
* @createDate 2024-11-09 00:39:29
*/
@Service
public class EventStatusLogsServiceImpl extends ServiceImpl<EventStatusLogsMapper, EventStatusLogs>
    implements EventStatusLogsService{

}




