package com.starlive.org.service.imp;

import com.starlive.org.enity.User;
import com.starlive.org.mapper.IUserLikesMapper;
import com.starlive.org.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserLikesMapper userLikesMapper;

    @Override
    public User findById(Integer id) {
        User user = userLikesMapper.findById(id);
        return user;
    }
}
