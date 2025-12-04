package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.pojo.Video;
import com.starlive.org.service.VideoService;
import com.starlive.org.mapper.VideoMapper;
import com.starlive.org.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author nan
* @description 针对表【video】的数据库操作Service实现
* @createDate 2024-10-14 18:05:28
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public long getVideoCount(String videoId) {
        //1.通过视频id查询redis
        Integer count = (Integer)redisUtil.get("video:" + videoId);
        //2.因为参数视频id是字符串，需要转换成int
        Integer content = Integer.parseInt(videoId);
        //3.如果redis中有，直接返回
        if(count != null){
            return count;
        }
        //4.如果redis中没有，查询数据库并更新到redis中
        count = videoMapper.getVideoCount(content);
        redisUtil.set("video:" + videoId,count);
        return count;
    }
}



