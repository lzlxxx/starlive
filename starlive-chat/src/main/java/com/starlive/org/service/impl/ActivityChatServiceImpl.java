package com.starlive.org.service.impl;

import com.starlive.org.entity.User;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.model.ChatMessage.MessageType;

import com.starlive.org.service.ActivityChatService;
//import com.starlive.org.service.ChatImageService;
import com.starlive.org.service.MessageSender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
public class ActivityChatServiceImpl implements ActivityChatService {
    
    @Autowired
    private MessageSender messageSender;
    
    @Autowired
    private MessageFileStorage messageStorage;
    

//   @Autowired
//    private ChatImageService chatImageService;

    // 存储活动的在线用户，key是活动ID，value是用户ID集合
    private final ConcurrentMap<String, Set<String>> activityUsers = new ConcurrentHashMap<>();
    
    // 新增：缓存活动中用户的用户名，key是活动ID，value是用户ID和用户名的映射
    private final ConcurrentMap<String, ConcurrentMap<String, String>> activityUserNames = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //清除用户在活动中的状态
    @Override
    public void leaveActivity(String userId, String activityId) {
        validateUserAndActivity(userId, activityId);
        messageSender.leaveActivity(userId);

    }

    @Override
    public void sendActivityMessage(ChatMessage message) {
        if (message == null || message.getActivityId() == null || message.getFromUserId() == null) {
            throw new IllegalArgumentException("Invalid message: missing required fields");
        }
        String activityId = message.getActivityId();

        // 设置消息属性
        message.setMessageId(UUID.randomUUID().toString());
        message.setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        
        // 发送消息(用户名会在MessageSender中自动设置)
        messageSender.sendToActivity(activityId, message);
    }


    private void validateUserAndActivity(String userId, String activityId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        validateActivityId(activityId);
    }

    private void validateActivityId(String activityId) {
        if (activityId == null || activityId.isEmpty()) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
    }

} 