package com.starlive.org.mapper;

import com.starlive.org.pojo.Likes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* @author nan
* @description 针对表【likes】的数据库操作Mapper
* @createDate 2024-10-16 00:09:45
* @Entity com.starlive.org.Pojo.Likes
*/
@Mapper
public interface LikesMapper extends BaseMapper<Likes> {
    @Delete("delete from likes where user_id = #{userId} and content_id = #{contentId}")
    public void deleteLikes(@Param("userId") Long userId,@Param("contentId") Long contentId);
    @Insert("insert into likes(user_id,content_id,status,create_time) values(#{userId},#{contentId},#{status},#{createTime})")
    public void insertLikes(@Param("userId") Long userId,@Param("contentId") Long contentId,@Param("status") Integer status,@Param("createTime") Date createTime);
}




