package com.starlive.org.mapper;

import com.starlive.org.pojo.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author nan
* @description 针对表【video】的数据库操作Mapper
* @createDate 2024-10-14 18:05:28
* @Entity com.starlive.org.Pojo.Video
*/
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    @Select("select likes from video where id=#{content}")
    public Integer getVideoCount(@Param("content") Integer content);

    @Select("select id,uploader_id,title,description,file_path,thumbnail_path,create_time,uploader_id,views,likes,total_collections,comments,tags,category,status,privacy,pv from video where id=#{content}")
    public Video getVideoById(@Param("content") Integer content);
    @Update("update video set total_collections=#{totalCollections} where id=#{content}")
    public void updateTotalCollections(@Param("content") Integer content,@Param("totalCollections") Integer totalCollections);

}




