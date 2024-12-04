package com.starlive.org.service.impl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.starlive.org.dto.FollowRequest;
import com.starlive.org.vo.FollowResult;
import com.starlive.org.vo.LikeAndFollowResult;

import com.starlive.org.pojo.Followers;
import com.starlive.org.pojo.Users;
import com.starlive.org.service.FollowersService;
import com.starlive.org.mapper.FollowersMapper;
import com.starlive.org.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FollowersServiceImpl extends MppServiceImpl<FollowersMapper, Followers> implements FollowersService {
    @Autowired
    private FollowersMapper followersMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Override                                                     //被关注者
    public Long follow(FollowRequest followRequest) {
        //1.获取所需参数   用户id    被关注者id   当前时间
        long userId = Long.parseLong(followRequest.getUserId());
        long authorId = Long.parseLong(followRequest.getAuthorId());
        Date date = new Date();
        //2.查询是否已经关注
        Followers data = followersMapper.findByDoubleId(userId, authorId);
        //3.如果已经关注就取消关注，否则就关注
        if(data!=null){
            //3.1  修改 修改时间    不删除但是两者关系置为0
            followersMapper.updateStatus(userId,authorId,date,0);
            //3.2  被关注者粉丝数-1
            Users users = usersMapper.getByUserIdUser(authorId);
            users.setFollowersCount(Math.max(0,users.getFollowersCount() - 1));
            Long followersCount = users.getFollowersCount();
            usersMapper.updateFollowersCountById(users.getUserId(),followersCount);//mapper里实现
            return followersCount;
        }
            //3.1 插入关注数据
            followersMapper.insertFollowers(userId,authorId,1,date,date);//mapper里实现
            //3.2  被关注者粉丝数+1
            Users users = usersMapper.getByUserIdUser(authorId);
            users.setFollowersCount(users.getFollowersCount() + 1);
            Long followersCount = users.getFollowersCount();
            usersMapper.updateFollowersCountById(users.getUserId(),followersCount);//mapper里实现
            return followersCount;
    }
}

