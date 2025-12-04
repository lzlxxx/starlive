package com.starlive.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starlive.org.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
     User getUserById(Long userId);

    boolean isFriend(Long userId, Long friendId);

    Map<Long, String> getUsernames(List<Long> userIds);
}
