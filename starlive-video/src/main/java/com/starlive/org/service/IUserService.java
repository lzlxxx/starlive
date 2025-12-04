package com.starlive.org.service;

import com.starlive.org.enity.User;

public interface IUserService {
    //根据id查询用户
    public User findById(Integer id);
}
