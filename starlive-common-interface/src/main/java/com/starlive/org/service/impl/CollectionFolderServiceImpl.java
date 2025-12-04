package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.vo.CollectionFolderInfo;

import com.starlive.org.pojo.CollectionFolder;

import com.starlive.org.service.CollectionFolderService;
import com.starlive.org.mapper.CollectionFolderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author nan
* @description 针对表【collection_folder】的数据库操作Service实现
* @createDate 2024-10-15 11:31:17
*/
@Service
public class CollectionFolderServiceImpl extends ServiceImpl<CollectionFolderMapper, CollectionFolder>
    implements CollectionFolderService {
    @Autowired
    private CollectionFolderMapper collectionFolderMapper;
    @Override
    public List<CollectionFolderInfo> getCollectionFolderList(Long userId) {
        //根据用户id从数据库中查询收藏夹列表
        List<CollectionFolderInfo> collectionFolderList = collectionFolderMapper.getCollectionFolderById(userId);
        if(collectionFolderList.isEmpty()){
          return null;
        }
        return collectionFolderList;
    }
}




