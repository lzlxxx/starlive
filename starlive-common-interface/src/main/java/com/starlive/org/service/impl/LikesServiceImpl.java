package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.pojo.Likes;

import com.starlive.org.service.LikesService;
import com.starlive.org.mapper.LikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author nan
* @description 针对表【likes】的数据库操作Service实现
* @createDate 2024-10-16 00:09:45
*/
@Service//操作点赞表
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {
    @Autowired
private LikesMapper likesMapper;
    @Override
    public void addLikes(Long userId, Long contentId) {
        //更新点赞表中的点赞记录：用户点赞了哪些视频
        likesMapper.insertLikes(userId,contentId,1,new Date());//mapper里实现
    }
    @Override
    public void deleteLikes(Long userId, Long contentId) {
        likesMapper.deleteLikes(userId,contentId);
    }

}




