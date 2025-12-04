package com.starlive.org.service.impl;

import com.starlive.org.vo.LikeAndFollowResult;
import com.starlive.org.dto.LikeRequest;
import com.starlive.org.errcode.BaseErrorCode;
import com.starlive.org.exception.ServiceException;
import com.starlive.org.service.LikeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LikeServiceImpl implements LikeService {
    private Map<String, LikeService> serviceMap;

    @Autowired
    public LikeServiceImpl(List<LikeService> strategies) {
        serviceMap = new HashMap<>();
        for (LikeService strategy : strategies) {
            String key = strategy.getClass().getSimpleName().replace("LikeStrategy", "").toLowerCase();
            serviceMap.put(key, strategy);
        }
    }
    public void Likes(LikeRequest likeRequest) {
        String type = likeRequest.getType();
        LikeService likeService = serviceMap.get(type);
        if (likeService == null) {
            throw new ServiceException("无效的类型", BaseErrorCode.CLIENT_ERROR);
        }
        likeService.Likes(likeRequest);
    }

}
