package com.starlive.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlive.org.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT user_id, username, password, email, phone, avatar_url, location, ip_address, is_test, bio, create_time, update_time " +
            "FROM users WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id", id = true),
            @Result(property = "location", column = "location", typeHandler = com.starlive.org.handler.GeoPointTypeHandler.class)
    })
    User getUser(@Param("userId") Long userId);
    
    @Select("SELECT user_id, username, password, email, phone, avatar_url, location, ip_address, is_test, bio, create_time, update_time " +
            "FROM users WHERE username = #{username}")
    @Results({
        @Result(property = "userId", column = "user_id", id = true),
        @Result(property = "location", column = "location", 
                typeHandler = com.starlive.org.handler.GeoPointTypeHandler.class)
    })
    User getUserByUsername(@Param("username") String username);
} 