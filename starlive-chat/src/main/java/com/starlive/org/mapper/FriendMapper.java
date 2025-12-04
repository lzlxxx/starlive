package com.starlive.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlive.org.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    
    @Select("SELECT friend_id FROM friends WHERE user_id = #{userId}")
    List<Long> getFriendIds(@Param("userId") Long userId);
    
    @Select("SELECT EXISTS(SELECT 1 FROM friends WHERE user_id = #{userId} AND friend_id = #{friendId})")
    boolean isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
} 