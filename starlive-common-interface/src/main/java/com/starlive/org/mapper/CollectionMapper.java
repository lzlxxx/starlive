package com.starlive.org.mapper;

import com.starlive.org.pojo.Collection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.Date;

/**
* @author nan
* @description 针对表【collection】的数据库操作Mapper
* @createDate 2024-10-15 09:20:24
* @Entity com.starlive.org.Pojo.Collection
*/
@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
    @Select("select collection_id , user_id ,content_id ,content_type ,folder_id ,create_time  from collection where collection_id = #{collectionId}")
    public Collection getByContentId(@Param("collectionId") Long contentId);
    @Delete("delete from collection where content_id = #{contentId}")
    public void deleteByContentId(@Param("contentId") Long contentId);
    @Insert("insert into collection(user_id,content_id,content_type,folder_id,create_time) values(#{userId},#{contentId},#{contentType},#{folderId},#{createTime})")
    public void insertOne( @Param("userId") Long userId, @Param("contentId") Long contentId, @Param("contentType") Object contentType, @Param("folderId") Long folderId,@Param("createTime") Date createTime);
}




