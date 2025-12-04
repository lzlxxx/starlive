package com.starlive.org.controller;

import com.starlive.org.model.UserPrivateVideoFavorites;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;

import com.starlive.org.service.UserPrivateVideoFavoritesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Api(value = "收藏视频接口", tags = "收藏视频接口")
public class UserPrivateVideoFavoritesController {

    private final UserPrivateVideoFavoritesService favoritesService;

    @Autowired
    public UserPrivateVideoFavoritesController(UserPrivateVideoFavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping("/user")
    @Operation(summary = "获取用户收藏接口")
    public WebResult getFavoritesByUserId(HttpServletRequest request) {

        List<UserPrivateVideoFavorites> favorites = favoritesService.getFavoritesByUserId(request);
        if (favorites.isEmpty()) {
            return WebResultUtil.failure().setMessage("没有收藏"); // 返回404没有收藏
        }
         else {
             return  WebResultUtil.success(favorites).setMessage("获取成功"); // 返回200和收藏列表
        }
    }

    @PostMapping("/addFavorite")
    @Operation(summary = "用户收藏视频接口")
    public WebResult<Void> addFavorite(@RequestBody UserPrivateVideoFavorites favorite) {
        favoritesService.addFavorite(favorite);
        return WebResultUtil.success(); // 返回200表示成功

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "用户删除收藏视频接口")
    public WebResult<Void> deleteFavorite(@PathVariable Long id) {
        favoritesService.deleteFavorite(id);
        return WebResultUtil.success(); // 返回200表示成功
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "用户更改收藏视频")
    public WebResult<Void> updateFavoriteStatus(@PathVariable Long id, @RequestParam String status) {
        favoritesService.updateFavoriteStatus(id, status);
        return WebResultUtil.success(); // 返回200表示成功
    }
}
