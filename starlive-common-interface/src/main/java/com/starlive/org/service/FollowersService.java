package com.starlive.org.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.starlive.org.dto.FollowRequest;
import com.starlive.org.vo.FollowResult;
import com.starlive.org.vo.LikeAndFollowResult;

import com.starlive.org.pojo.Followers;

public interface FollowersService extends IMppService<Followers> {
    public Long follow(FollowRequest followRequest);
}
