package com.starlive.org.service.impl;

import com.starlive.org.entity.User;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.service.MessageSender;
import com.starlive.org.service.RoomChatService;
import com.starlive.org.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
public class RoomChatServiceImpl implements RoomChatService {



    @Autowired
    private MessageSender messageSender;

    @Override
    public void sendMessage(ChatMessage message) {
        String roomId = message.getRoomId();
            message.setMessageId(UUID.randomUUID().toString());
            // 发送消息给房间内的所有用户
            messageSender.sendToRoom(roomId, message);
    }



    @Override
    public void leaveRoom(String userId, String roomId) {
        validateUserIds(userId);
        validateRoomId(roomId);
        messageSender.leaveRoom(userId);
        log.info("User {} left room {}", userId, roomId);
    }



    private void validateMessage(ChatMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("消息不能为空");
        }
        if (message.getFromUserId() == null || message.getFromUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("发送者ID不能为空");
        }
        if (message.getRoomId() == null || message.getRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("房间ID不能为空");
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

    private void validateRoomId(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("房间ID不能为空");
        }
    }
} 