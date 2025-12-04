package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.dto.FavoriteRequest;
import com.starlive.org.vo.FavoriteResult;

import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.pojo.*;
import com.starlive.org.exception.ServiceException;


import com.starlive.org.service.CollectionService;
import com.starlive.org.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;

/**
* @author nan
* @description 针对表【collection】的数据库操作Service实现
* @createDate 2024-10-15 09:20:24
*/
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private CollectionFolderMapper collectionFolderMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private LiveCountMapper liveCountMapper;

    @Override
    public FavoriteResult Collection(FavoriteRequest favoriteRequest) {
        //1.获取参数信息 用户id 操作类型 收藏内容类型 收藏内容id 收藏夹名称 收藏夹id
              //收藏夹名称不为空的时候代表新建收藏夹  只有收藏夹id的时候代表选择已有的收藏夹
        Long userId = Long.parseLong(favoriteRequest.getUserId());
        int action = favoriteRequest.getAction();
        String type = favoriteRequest.getType();
        Long contentId = Long.parseLong(favoriteRequest.getContentId());
        String folderName = favoriteRequest.getFolderName();
        Long folderId = favoriteRequest.getFolderId() != null ? Long.parseLong(favoriteRequest.getFolderId()) : null;
        // 2.判断操作类型，1 为添加收藏，0 为取消收藏
        if (action == 1) {
            //2.1  添加收藏操作
            return handleAddCollection(userId, contentId, type, folderName, folderId);
        } else {
            //2.2 取消收藏操作
            return handleRemoveCollection(contentId, type);
        }
    }

    private FavoriteResult handleAddCollection(Long userId, Long contentId, String type, String folderName, Long folderId) {
       //2.1.1 获取当前时间
        Date date = new Date();
        CollectionFolder collectionFolder;
        // 2.1.2 判断收藏夹名称是否为空，如果为空则创建新的收藏夹，否则查找已有的收藏夹
        if (folderName != null) {
            collectionFolder = createNewFolder(userId, folderName, type, date);
            folderId = collectionFolder.getFolderId();
        } else {
            //2.1.2  收藏夹名称为空 查找已有的收藏夹
            collectionFolder = collectionFolderMapper.getCollectionFolderByFolderId(folderId);//mapper实现
            if (collectionFolder == null || !collectionFolder.getFolderType().equals(type)) {
                throw new ServiceException("收藏夹类型与收藏内容类型不匹配",BaseErrorCode.CLIENT_ERROR);
            }
        }
        // 2.1.3 数据库中插入收藏记录
        insertCollection(userId, contentId, type, folderId, date);

        // 2.1.4 更新收藏夹的收藏数以及时间
        collectionFolder.setCollectionCount(collectionFolder.getCollectionCount() + 1);
        collectionFolderMapper.updateCollectionCount(collectionFolder.getFolderId(),collectionFolder.getCollectionCount(),date);//////////////////在mapper里实现

        //2.1.5 增加视频或直播间的总收藏数
        Integer totalCount = ChangeFavoriteCount(contentId, type, 1);

        return new FavoriteResult(folderId, collectionFolder.getFolderName(), totalCount, "added", true);
    }

    private FavoriteResult handleRemoveCollection(Long contentId, String type) {
        //2.2.1 获取当前时间
        Date date = new Date();
        //2.2.2 根据内容id查找收藏记录
        Collection collection = collectionMapper.getByContentId(contentId);
        if (collection == null) {
            throw new ServiceException("没收藏过",BaseErrorCode.CLIENT_ERROR);
        }

        // 2.2.3 获取该视频的所存的收藏夹信息
        CollectionFolder collectionFolder = collectionFolderMapper.selectById(collection.getFolderId());//mapper实现
        if (collectionFolder == null) {
            throw new ServiceException("收藏夹不存在", BaseErrorCode.CLIENT_ERROR);
        }

        // 2.2.4 更新收藏夹的收藏数
        collectionFolder.setCollectionCount(Math.max(0, collectionFolder.getCollectionCount() - 1));
        collectionFolder.setUpdateTime(date);
        collectionFolderMapper.updateById(collectionFolder);//mapper里实现

        // 2.2.5 减少视频或直播间的总收藏数
        Integer totalCount = ChangeFavoriteCount(contentId, type, -1);

        // 2.2.6删除收藏记录
        collectionMapper.deleteByContentId(collection.getCollectionId());//mapper里实现

        return new FavoriteResult(collection.getFolderId(), collectionFolder.getFolderName(), totalCount, "取消收藏成功", false);
    }
       //创建新的文件夹
    private CollectionFolder createNewFolder(Long userId, String folderName, String type, Date date) {
        CollectionFolder newFolder = new CollectionFolder();
        newFolder.setUserId(userId);
        newFolder.setFolderName(folderName);
        newFolder.setFolderType(type);
        newFolder.setCollectionCount(0);
        newFolder.setCreateTime(date);
        newFolder.setUpdateTime(date);
        collectionFolderMapper.insertOne(userId,0,folderName,type,date,date);//mapper里实现
        return newFolder;
    }
//插入收藏记录
    private void insertCollection(Long userId, Long contentId, String type, Long folderId, Date date) {
        collectionMapper.insertOne(userId,contentId,type,folderId,date);;//mapper里实现
    }
    //增减总收藏数
    private Integer ChangeFavoriteCount(Object contentId, String type, int x) {
        if ("video".equals(type)) {
            //1.获取视频信息
            Video video = videoMapper.getVideoById((int)contentId);//mapper里实现
            if (video != null) {
                //2.增删总收藏数
                video.setTotalCollections(video.getTotalCollections() + x);
                videoMapper.updateTotalCollections(video.getId(),video.getTotalCollections());//mapper里实现
                //3.返回视频总收藏数
                return video.getTotalCollections();
            }
            throw new ServiceException("视频不存在", BaseErrorCode.CLIENT_ERROR);
        } else {
            //同视频操作
            LiveCount liveCount = liveCountMapper.getLiveCountById((long)contentId);
            if (liveCount != null) {
                liveCount.setLiveFollows(liveCount.getLiveFollows() + x);
                liveCountMapper.updateLiveCount(liveCount.getId(), liveCount.getLiveLikes());
                return Math.toIntExact(liveCount.getLiveFollows());
            }
            throw new ServiceException("直播间不存在", BaseErrorCode.CLIENT_ERROR);
        }
    }
}