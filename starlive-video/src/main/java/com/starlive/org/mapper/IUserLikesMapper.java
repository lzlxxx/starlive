package com.starlive.org.mapper;

import com.starlive.org.enity.User;
import com.starlive.org.enity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IUserLikesMapper {

    //根据用户返回对象
    @Select("SELECT * FROM users WHERE user_id = #{id}")
    User findById(Integer id);

    //查询like表
    @Select("SELECT content_id FROM likes WHERE user_id = #{id} LIMIT #{offset}, #{pageSize}")
    List<Integer> getLikesList(@Param("id") Integer id, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);




    //批量查询
    @Select("<script>" +
            "SELECT * FROM video WHERE id IN " +
            "<foreach item='item' collection='ids' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    public List<Video> findVideos(@Param("ids")List<Integer> ids);
}
