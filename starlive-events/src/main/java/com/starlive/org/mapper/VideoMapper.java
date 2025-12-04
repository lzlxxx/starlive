package com.starlive.org.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.starlive.org.pojo.Video;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 视频信息表 Mapper 接口
 * </p>
 *
 * @author meng
 * @since 2024-10-29
 */
@Mapper
@Repository
public interface VideoMapper extends BaseMapper<Video> {


    @Select("SELECT id, uploader_id, title, description, file_path, thumbnail_path, create_time, upload_time, duration, views, likes, comments, tags, category, status, privacy, pv ,bind_event FROM video WHERE id=#{videoId}")
    @Results(id = "VideoResultMap",value = {
            @Result(property = "videoId", column = "id"),
            @Result(property = "uploaderId", column = "uploader_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "thumbnailPath", column = "thumbnail_path"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "uploadTime", column = "upload_time"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "views", column = "views"),
            @Result(property = "likes", column = "likes"),
            @Result(property = "comments", column = "comments"),
            @Result(property = "tags", column = "tags"),
            @Result(property = "category", column = "category"),
            @Result(property = "status", column = "status"),
            @Result(property = "privacy", column = "privacy"),
            @Result(property = "pv", column = "pv"),
            @Result(property = "eventId",column = "bind_event")
    })
    Video selectVideo(@Param("videoId") int id);

    @Select("SELECT id, uploader_id, title, description, file_path, thumbnail_path, create_time, upload_time, duration, views, likes, comments, tags, category, status, privacy, pv ,bind_event" +
            " FROM video WHERE bind_event=#{eventId}")
    @ResultMap("VideoResultMap")
    List<Video> selectVideoList(@Param("eventId") Long eventId);

    @Update("update video set bind_event= #{eventId} where id = #{id}")
    void updateEventId(@Param("eventId") Long eventId,@Param("id") Integer id);
}
