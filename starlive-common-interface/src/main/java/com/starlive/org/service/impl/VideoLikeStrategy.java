package com.starlive.org.service.impl;

import cn.hutool.bloomfilter.BloomFilterUtil;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.LikeAndFollowResult;
import com.starlive.org.dto.LikeRequest;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.pojo.Video;
import com.starlive.org.service.LikeService;
import com.starlive.org.service.LikesService;
import com.starlive.org.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class VideoLikeStrategy implements LikeService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private LikesService likesService;
    @Autowired
    private RedisUtil redisUtil;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    @Override
    public void Likes( LikeRequest likeRequest) {
        //1.获取需要的参数  用户id  视频id   点赞数   是否取消点赞
        long userId = Long.parseLong(likeRequest.getUserId());
        String contentId = likeRequest.getContentId();
        int num = likeRequest.getNum();
        int isCancel = likeRequest.getIsCancel();
        //2.查询数据库是否有这个视频    该方法需要在mapper中实现
        Video video = videoMapper.selectById(Integer.parseInt(contentId));
        if (video == null) {
          throw new ServiceException("没找到这个视频", BaseErrorCode.CLIENT_ERROR);
        }
        //3.判断操作类型  1点赞  0取消点赞
        if (isCancel == 1) {
            //3.1.1 在redis中用hash存储点赞记录
            redisUtil.hset(likeRequest.getUserId()+":video", contentId, "1");
            //3.1.2 先删除视频的点赞数缓存
            redisUtil.delete("video:"+contentId);
            //3.1.3 更新数据库中的点赞数
            video.setLikes(video.getLikes() + num);
            videoMapper.updateById(video);//代码在mapper集合中实现
            //3.1.4 更新点赞表中的点赞记录：用户点赞了哪些视频
            likesService.addLikes(userId, Long.parseLong(contentId));
            //3.1.5 再次删除视频的点赞数缓存
            scheduler.schedule(() -> {
                redisUtil.delete("video:"+contentId);
            }, 500, TimeUnit.MILLISECONDS);

        } else {
            //3.0.1 删除redis中的点赞记录
            redisUtil.hdel(likeRequest.getUserId()+":video", contentId);
            //3.0.2 先删除视频的点赞数缓存
            redisUtil.delete("video:"+contentId);
            //3.0.3 更新数据库中的点赞数
            int likes = Math.max(0, video.getLikes() - num);
            video.setLikes(likes);
            videoMapper.updateById(video);
            //3.0.4 更新点赞表中的点赞记录：用户点赞了哪些视频
            likesService.deleteLikes(userId,Long.parseLong(contentId)); //删除点赞表中的点赞记录
            //3.0.5 再次删除视频的点赞数缓存
            scheduler.schedule(() -> {
                redisUtil.delete("video:"+contentId);
            }, 500, TimeUnit.MILLISECONDS);
        }
    }


}
