package com.starlive.org.service.impl;

import com.starlive.org.entity.User;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.service.MessageSender;
import com.starlive.org.service.PrivateChatService;
import com.starlive.org.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class PrivateChatServiceImpl implements PrivateChatService {
    
    @Autowired
    private MessageFileStorage messageStorage;
    
    @Autowired
    private MessageSender messageSender;
    
    private final ConcurrentMap<String, Boolean> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(ChatMessage message) {
        validateMessage(message);
        message.setMessageId(UUID.randomUUID().toString());
        message.setType(ChatMessage.MessageType.PRIVATE_CHAT);

        messageSender.sendToUser(message.getToUserId(), message);
        
        log.debug("Private message sent from {} to {}", 
                message.getFromUserId(), message.getToUserId());
    }



    private void validateMessage(ChatMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("消息不能为空");
        }
        if (message.getFromUserId() == null || message.getFromUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("发送者ID不能为空");
        }
        if (message.getToUserId() == null || message.getToUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }

    private void validateUserIds(String... userIds) {
        for (String userId : userIds) {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
        }
    }
} 