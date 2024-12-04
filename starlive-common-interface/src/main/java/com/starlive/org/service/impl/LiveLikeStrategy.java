package com.starlive.org.service.impl;

import com.starlive.org.util.RedisUtil;
import com.starlive.org.dto.LikeRequest;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.pojo.LiveCount;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.service.LikeService;
import com.starlive.org.mapper.LiveCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LiveLikeStrategy implements LikeService {
    @Autowired
    private LiveCountMapper liveCountMapper;
    @Autowired
    private RedisUtil redisUtil;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @Override
    public void Likes( LikeRequest likeRequest) {
        //1.获取所需参数  直播间id    点赞数
        String contentId = likeRequest.getContentId();
        int num = likeRequest.getNum();
        LiveCount liveCount = liveCountMapper.getLiveCountById(Long.parseLong(contentId));
        if (liveCount == null) {
            throw new ServiceException("没找到直播间", BaseErrorCode.CLIENT_ERROR);
        }
        //2.redis对直播间进行计数操作
        redisUtil.increment("live:" + contentId, num);
        //3.延迟500毫秒将redis中的数据持久化到数据库中
        schedulePersistence(contentId, liveCount);
    }
    private void  schedulePersistence(String contentId, LiveCount liveCount){
        scheduler.schedule(() -> {
            boolean success = false;
            int i = 0;
            while (i < 3 && !success) {
                try {
                    i++;
                    Integer count=(Integer)redisUtil.get("live:" + contentId); // 获取当前点赞数
                    int row= liveCountMapper.updateLiveCount(liveCount.getId(),count.longValue());// 更新数据库中的点赞数    自己实现
                    if (row > 0) {
                        success = true;
                    } else {
                        throw new Exception("数据库更新失败");
                    }
                } catch (Exception e) {
                    if (i == 3) {
                        throw new ServiceException("直播间点赞数持久化失败", e, BaseErrorCode.SERVICE_ERROR);
                    }
                    try {
                        Thread.sleep(500); // 等待500毫秒再重试
                    } catch (Exception ee) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }, 500, TimeUnit.MILLISECONDS); // 延迟500毫秒后执行
    }
}

