package com.starlive.org.service;

import com.starlive.org.mapper.UserPrivateVideoFavoriteMapper;
import com.starlive.org.model.UserPrivateVideoFavorites;
import com.starlive.org.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPrivateVideoFavoritesService {

    private final UserPrivateVideoFavoriteMapper favoritesMapper;

    @Autowired
    public UserPrivateVideoFavoritesService(UserPrivateVideoFavoriteMapper favoritesMapper) {
        this.favoritesMapper = favoritesMapper;
    }

    @Cacheable(value = "favoritesByUser", key = "#request.cookies[(@.name == 'token')].value", unless = "#result == null")
    public List<UserPrivateVideoFavorites> getFavoritesByUserId(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return null;
        }
        return favoritesMapper.selectByUserId(userId);
    }


    @CachePut(value = "favoritesByUser", key = "#favorite.userId")
    public void addFavorite(UserPrivateVideoFavorites favorite) {
        favorite.setCreated_At(LocalDateTime.now());
        favorite.setUpdated_At(LocalDateTime.now());
        favoritesMapper.insert(favorite);
    }

    @CacheEvict(value = "favoritesByUser", key = "#id", allEntries = true)
    public void deleteFavorite(Long id) {
        favoritesMapper.deleteById(id);
    }

    @CachePut(value = "favorites", key = "#id")
    public void updateFavoriteStatus(Long id, String status) {
        favoritesMapper.updateStatusById(id, status, LocalDateTime.now());
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    try {
                        return Long.parseLong(JwtUtil.parseToken(cookie.getValue()).getSubject());
                    } catch (Exception e) {
                        // Handle token parsing error
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
