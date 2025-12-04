package com.starlive.org.mapper;

import com.starlive.org.model.UserPrivateVideoFavorites;
import org.apache.ibatis.annotations.*;



import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserPrivateVideoFavoriteMapper {

    @Insert("INSERT INTO user_private_video_favorites(user_id, video_id, is_private, created_at, updated_at, status, notes) " +
            "VALUES (#{userId}, #{videoId}, #{isPrivate}, #{createdAt}, #{updatedAt}, #{status}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPrivateVideoFavorites favorite);

    @Delete("DELETE FROM user_private_video_favorites WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE user_private_video_favorites SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatusById(@Param("id") Long id, @Param("status") String status, @Param("updatedAt") LocalDateTime updatedAt);

    @Select("SELECT * FROM user_private_video_favorites WHERE id = #{id}")
    UserPrivateVideoFavorites selectById(@Param("id") Long id);

    @Select("SELECT * FROM user_private_video_favorites WHERE user_id = #{userId}")
    List<UserPrivateVideoFavorites> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_private_video_favorites WHERE user_id = #{userId} AND video_id = #{videoId}")
    UserPrivateVideoFavorites selectByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Integer videoId);
}
