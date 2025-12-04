package com.starlive.org.service.impl;

import com.starlive.org.handler.ChatMessageHandler;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.service.ChatService;
import com.starlive.org.service.MessageSender;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

/**
 * 聊天服务实现类
 * 实现了聊天系统的核心业务逻辑
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageFileStorage messageStorage;

    @Autowired
    private MessageSender messageSender;


    @Override
    public int getActivityUserCount(String activityId) {
        // 实现获取活动群聊在线用户数的逻辑
        ConcurrentMap<String, Channel> activityChannels = messageSender.getActivityChannels(activityId);
        if(activityChannels != null) {
            return activityChannels.size();
        }
        return 0;
    }

    @Override
    public int getRoomOnlineCount(String roomId) {
        // 实现获取房间在线用户数的逻辑
        ConcurrentMap<String, Channel> roomChannels = messageSender.getRoomChannels(roomId);
        if(roomChannels != null) {
            return roomChannels.size();
        }
        return 0;
    }

    @Override
    public boolean isUserOnline(String userId) {
        // 实现检查用户是否在线的逻辑
        return false; // 待实现
    }


}