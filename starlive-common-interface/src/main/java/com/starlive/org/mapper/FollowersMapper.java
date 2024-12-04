package com.starlive.org.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.starlive.org.pojo.Followers;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface FollowersMapper extends MppBaseMapper<Followers> {
    @Select(" select follower_id, followed_id, status, create_time, update_time from followers where follower_id = #{userId} and followed_id = #{authorId}")
    public Followers findByDoubleId(@Param("userId") long userId, @Param("authorId") long authorId );
   @Update("update followers set status = #{status}, update_time = #{updateTime} where follower_id = #{userId} and followed_id = #{authorId}")
    public void updateStatus(@Param("userId") long userId, @Param("authorId") long authorId,@Param("updateTime") Date updateTime,@Param("status") int status);
    @Insert("insert into followers(follower_id,followed_id,status,create_time,update_time) values(#{userId},#{authorId},#{status},#{createTime},#{updateTime})")
    public void insertFollowers(@Param("userId") long userId, @Param("authorId") long authorId,@Param("status") int status,@Param("createTime") Date createTime,@Param("updateTime") Date updateTime);
}
