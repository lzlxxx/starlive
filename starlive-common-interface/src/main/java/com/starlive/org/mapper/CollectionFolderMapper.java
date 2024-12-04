package com.starlive.org.mapper;

import com.starlive.org.vo.CollectionFolderInfo;
import com.starlive.org.pojo.CollectionFolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
* @author nan
* @description 针对表【collection_folder】的数据库操作Mapper
* @createDate 2024-10-15 11:31:17
* @Entity com.starlive.org.Pojo.CollectionFolder
*/
@Mapper
public interface CollectionFolderMapper extends BaseMapper<CollectionFolder> {
   @Select("select folder_id,folder_name,collection_count from collection_folder where user_id = #{userId}")
   public List<CollectionFolderInfo>getCollectionFolderById(@Param("userId")Long userId);
   @Select("select folder_id,user_id,collection_count,folder_name,folder_type,create_time,update_time from collection_folder where folder_id = #{folderId}")
   public CollectionFolder getCollectionFolderByFolderId(@Param("folderId") Long folderId);
   @Update("update collection_folder set collection_count = #{collectionCount} and update_time = #{updateTime} where folder_id = #{folderId}")
   public void updateCollectionCount(@Param("folderId") Long folderId,@Param("collectionCount") Integer collectionCount,@Param("updateTime")  Date updateTime);
   @Insert("insert into collection_folder(user_id,collection_count,folder_name,folder_type,create_time,update_time) values(#{userId},#{collectionCount},#{folderName},#{folderType},#{createTime},#{updateTime})")
   public void insertOne(@Param("userId") Long userId,@Param("collectionCount") Integer collectionCount,@Param("folderName") String folderName,@Param("folderType") String folderType,@Param("createTime") Date createTime,@Param("updateTime") Date updateTime);
}