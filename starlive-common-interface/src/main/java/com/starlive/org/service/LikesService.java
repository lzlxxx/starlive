package com.starlive.org.service;

import com.starlive.org.pojo.Likes;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
* @author nan
* @description 针对表【likes】的数据库操作Service
* @createDate 2024-10-16 00:09:45
*/
public interface LikesService extends IService<Likes> {
    public void addLikes(Long userId, Long contentId);
    public void deleteLikes(@Param("userId") Long userId, @Param("contentId") Long contentId);

}
