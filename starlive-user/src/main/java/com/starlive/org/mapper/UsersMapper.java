package com.starlive.org.mapper;

import com.starlive.org.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author meng
 * @since 2024-10-13
 */
@Repository
public interface UsersMapper extends BaseMapper<Users> {

    @Select("select user_id,username,password,email,avatar_url,location,ip_address,is_test,bio,create_time,update_time,phone from users where phone=#{phone}")
    Users selectByPhone(@Param("phone") String phone);

    @Select("select user_id,username,password,email,avatar_url,location,ip_address,is_test,bio,create_time,update_time,phone from users where user_id=#{userId};")
    Users selectByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO users (username, location, create_time, update_time, phone) " +
            "VALUES (#{username}, #{location, typeHandler=com.starlive.org.handler.GeoPointTypeHandler}, NOW(), NOW(), #{phone})")
    void addUser(Users users);
    @Select("SELECT user_id, username, password, email, phone, avatar_url, location, ip_address, is_test, bio, create_time, update_time " +
            "FROM users WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id", id = true),
            @Result(property = "location", column = "location", typeHandler = com.starlive.org.handler.GeoPointTypeHandler.class)
    })
    Users getUser(@Param("userId") Long userId);

    @Update("update users set username= #{username} where user_id = #{userId}")
    void updateUserName(@Param("username") String username, @Param("userId") Long userId);

    @Update("UPDATE users SET avatar_url = #{url} WHERE user_id = #{userId}")
    void updateUserUrl(@Param("userId") Long userId, @Param("url") String url);


}
