package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.pojo.LiveCount;

import com.starlive.org.service.LiveCountService;
import com.starlive.org.mapper.LiveCountMapper;
import com.starlive.org.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author nan
* @description 针对表【live_count】的数据库操作Service实现
* @createDate 2024-10-15 18:04:15
*/
@Service
public class LiveCountServiceImpl extends ServiceImpl<LiveCountMapper, LiveCount>
    implements LiveCountService {
    @Autowired
    private  LiveCountMapper liveCountMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public Long getLiveCount(String liveId) {
        //1.先从redis中获取
        Long count=(Long)redisUtil.get("live:"+liveId);
        //2.如果redis中有，则直接返回
        if(count!=null){
            return count;
        }
        //3.如果redis中没有，则从数据库中获取
        long Id= Long.parseLong(liveId);
        count=liveCountMapper.getLiveCount(Id);
        //4.将数据库中的数据存入redis中
        redisUtil.set("live:"+liveId,count);
        return count;

    }
}




