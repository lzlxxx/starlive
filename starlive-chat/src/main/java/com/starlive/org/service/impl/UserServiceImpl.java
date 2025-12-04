package com.starlive.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starlive.org.entity.User;
import com.starlive.org.mapper.UserMapper;
import com.starlive.org.mapper.FriendMapper;
import com.starlive.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private FriendMapper friendMapper;
    @Override
    public User getUserById(Long userId) {
        return userMapper.getUser(userId);
    }
    
    public List<User> getFriends(Long userId) {
        List<Long> friendIds = friendMapper.getFriendIds(userId);
        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return friendMapper.isFriend(userId, friendId);
    }
    @Override
    public Map<Long, String> getUsernames(List<Long> userIds) {
        List<User> users = userIds.stream()
                .map(this::getUserById)
                .toList();
        return users.stream().collect(
            Collectors.toMap(User::getUserId, User::getUsername)
        );
    }
} 