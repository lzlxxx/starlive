package com.starlive.org.mapper;

import com.starlive.org.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
* @author nan
* @description 针对表【users】的数据库操作Mapper
* @createDate 2024-10-14 21:23:51
* @Entity com.starlive.org.Pojo.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
      @Select("select user_id,username,password,email,avatar_url,location,ip_address,is_test,bio,create_time,update_time,followers_count from users where user_id = #{userId}")
      @Results({
              @Result(property = "userId", column = "user_id",id = true),
              @Result(property = "location", column = "location", typeHandler =com.starlive.org.handler.GeoPointTypeHandler.class )
      })
      public Users getByUserIdUser(@Param("userId") Long userId);

      @Update("update users set followers_count =#{count} where user_id = #{userId}")
      public  void updateFollowersCountById(@Param("userId") Long userId,@Param("count") Long count);
}




