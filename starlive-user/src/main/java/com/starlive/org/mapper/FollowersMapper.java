package com.starlive.org.mapper;

import com.starlive.org.entity.Followers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author meng
 * @since 2024-10-13
 */
public interface FollowersMapper extends BaseMapper<Followers> {
    @Select("select * from followers where follower_id=#{followerId}")
    Followers selectFollowers(Long followerId);

    @Insert("insert into followers(follower_id,followed_id) values (#{followerId},#{followedId})")
    void insertFollowers(Followers followers);

}
