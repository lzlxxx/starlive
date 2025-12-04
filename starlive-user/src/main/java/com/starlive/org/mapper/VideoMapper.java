package com.starlive.org.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlive.org.entity.Video;
import com.starlive.org.vo.WorkInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
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

@Repository
public interface VideoMapper extends BaseMapper<Video> {

    @Select("select file_path, thumbnail_path,likes,tags from video where  uploader_id=#{userId}")
    @Results({

            @Result(property = "file_path",column = "file_path"),
            @Result(property = "thumbnail_path",column = "thumbnail_path"),
            @Result(property = "likes",column = "likes"),
            @Result(property = "tags",column = "tags")
    }
    )
    List<WorkInfoVo> getWorkInfo(@Param("userId") Long userId);

    @Select("select COUNT(*) as video_count from video where uploader_id=#{userId}")
    Integer getVideoCount(@Param("userId") Long userId);

}
