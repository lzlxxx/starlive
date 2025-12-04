package com.starlive.org.service;
import com.starlive.org.pojo.LiveCount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author nan
* @description 针对表【live_count】的数据库操作Service
* @createDate 2024-10-15 18:04:15
*/
public interface LiveCountService extends IService<LiveCount> {
    public Long getLiveCount(String liveId);
}

