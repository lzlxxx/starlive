package com.starlive.org.service;

import com.starlive.org.vo.CollectionFolderInfo;

import com.starlive.org.pojo.CollectionFolder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author nan
* @description 针对表【collection_folder】的数据库操作Service
* @createDate 2024-10-15 11:31:17
*/
public interface CollectionFolderService extends IService<CollectionFolder> {
    public List<CollectionFolderInfo> getCollectionFolderList(Long userId);


}
