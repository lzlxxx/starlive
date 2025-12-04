package com.starlive.org.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlive.org.enity.Video;
import org.apache.ibatis.annotations.*;


import java.util.List;
@Mapper

public interface VideoMapper extends BaseMapper<Video> {
    @Insert("INSERT INTO videos (uploader_id, title, description, file_path, thumbnail_path, create_time, upload_time, duration, views, likes, total_collections, comments, tags, category, status, privacy, pv) " +
            "VALUES (#{uploaderId}, #{title}, #{description}, #{filePath}, #{thumbnailPath}, #{createTime}, #{uploadTime}, #{duration}, #{views}, #{likes}, #{totalCollections}, #{comments}, #{tags}, #{category}, #{status}, #{privacy}, #{pv})")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // 自动生成主键
    void insertVideo(Video video);

    @Select("SELECT * FROM videos WHERE id = #{id}")
    Video findById(int id);

    @Select("SELECT * FROM videos")
    List<Video> findAll();

    @Update("UPDATE videos SET title = #{title}, description = #{description}, file_path = #{filePath}, " +
            "thumbnail_path = #{thumbnailPath}, upload_time = #{uploadTime}, duration = #{duration}, " +
            "views = #{views}, likes = #{likes}, total_collections = #{totalCollections}, comments = #{comments}, " +
            "tags = #{tags}, category = #{category}, status = #{status}, privacy = #{privacy}, pv = #{pv} " +
            "WHERE id = #{id}")
    void updateVideo(Video video);

    @Delete("DELETE FROM videos WHERE id = #{id}")
    void deleteById(int id);
    @Select("select likes from video where id=#{content}")
    public Integer getVideoCount(@Param("content") Integer content);

    @Select("select id,uploader_id,title,description,file_path,thumbnail_path,create_time,uploader_id,views,likes,total_collections,comments,tags,category,status,privacy,pv from video where id=#{content}")
    public Video getVideoById(@Param("content") Integer content);
    @Update("update video set total_collections=#{totalCollections} where id=#{content}")
    public void updateTotalCollections(@Param("content") Integer content,@Param("totalCollections") Integer totalCollections);

}
