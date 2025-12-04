package com.starlive.org.service;

import com.starlive.org.dto.FavoriteRequest;
import com.starlive.org.vo.FavoriteResult;

import com.starlive.org.pojo.Collection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author nan
* @description 针对表【collection】的数据库操作Service
* @createDate 2024-10-15 09:20:24
*/
public interface CollectionService extends IService<Collection> {
    public FavoriteResult Collection(FavoriteRequest favoriteRequest);


}
