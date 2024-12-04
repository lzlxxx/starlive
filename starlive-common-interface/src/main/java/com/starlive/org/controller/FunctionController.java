package com.starlive.org.controller;

import com.starlive.org.dto.*;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.*;

import com.starlive.org.service.impl.LikeServiceImpl;
import com.starlive.org.vo.CollectionFolderInfo;
import com.starlive.org.vo.FavoriteResult;
import com.starlive.org.vo.LikeAndFollowResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/function")
@Validated
public class FunctionController {

    @Autowired
    public VideoService videoService;
    @Autowired
    public LiveCountService liveCountService;
    @Autowired
    public FollowersService followService;
    @Autowired
    public CollectionService collectionService;
    @Autowired
    public CollectionFolderService collectionFolderService;
    @Autowired
    public LikeServiceImpl likeService;
   @PostMapping("/like")
    public WebResult<Void> like(@RequestBody LikeRequest likeRequest) {
       likeService.Likes(likeRequest);
       return WebResultUtil.success();
    }
    @PostMapping("/follow")
   public WebResult<Long>  follow(@RequestBody FollowRequest followRequest) {
       return WebResultUtil.success(followService.follow(followRequest));
    }
    @PostMapping("/favorite")
    public WebResult<FavoriteResult> favorite(@RequestBody FavoriteRequest favoriteRequest){
        FavoriteResult favoriteResult=null;
        try {
            favoriteResult = collectionService.Collection(favoriteRequest);
       }catch (ServiceException e){
           return WebResultUtil.failure(e);
       }
       return WebResultUtil.success(favoriteResult);
    }
    @GetMapping("/getList")
    public WebResult<List<CollectionFolderInfo>> getCollectionFolderList(@RequestParam(value = "userId",required = true) String userId){
       return  WebResultUtil.success(collectionFolderService.getCollectionFolderList(Long.parseLong(userId.trim())));
    }
    @GetMapping("/getCount")
    public WebResult<Long> getLiveCount(@RequestParam(value = "contentId",required = true)String contentId,@RequestParam(value = "type",required = true) String type){
       switch (type){
           case "video":
               return WebResultUtil.success(videoService.getVideoCount(contentId));
           case "live":
               return WebResultUtil.success(liveCountService.getLiveCount(contentId));
       }
       return null;
   }}
