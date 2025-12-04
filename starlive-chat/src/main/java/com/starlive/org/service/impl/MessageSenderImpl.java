package com.starlive.org.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.config.MinioConfig;
import com.starlive.org.entity.User;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.service.MessageSender;
import com.starlive.org.service.UserService;
import io.minio.MinioClient;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageSenderImpl implements MessageSender {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageFileStorage messageStorage;

    @Autowired
    private UserService userService;
    @Autowired
    private MinioServiceImpl minioService;
    @Autowired
    private MinioConfig minioConfig;

    // 为不同类型的聊天创建不同的channel映射
    private final Map<String, Set<Channel>> privateChannels = new ConcurrentHashMap<>();
    
    // 活动聊天：外层Map的key是activityId，内层Map的key是userId
    private final Map<String, ConcurrentMap<String, Channel>> activityChannels = new ConcurrentHashMap<>();
    
    // 直播间聊天：外层Map的key是roomId，内层Map的key是userId
    private final Map<String, ConcurrentMap<String, Channel>> roomChannels = new ConcurrentHashMap<>();
    
    // 缓存用户名,key是userId,value是userName
    private final ConcurrentMap<String, String> userNameCache = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void bindUserChannel(String userId, Channel channel) {
        String type = (String) channel.attr(AttributeKey.valueOf("chatType")).get();
        String chatId = (String) channel.attr(AttributeKey.valueOf("chatId")).get();
        String userName = userService.getUserById(Long.valueOf(userId)).getUsername();
        
        if (userName != null) {
            userNameCache.put(userId, userName);
        }
        
        if ("PRIVATE".equals(type)) {
            privateChannels.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(channel);
            log.debug("User {} bound to private channel {}", userId, channel.id());
        } else if ("ACTIVITY".equals(type)) {
            // 绑定到活动channel，使用userId作为key
            activityChannels.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>())
                    .put(userId, channel);
            
            sendToActivity(chatId, ChatMessage.builder()
                    .activityId(chatId)
                    .fromUserId(userId)
                    .fromUserName(userName)
                    .content(userName+" 加入了活动聊天")
                    .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                    .messageId(UUID.randomUUID().toString())
                    .type(ChatMessage.MessageType.JOIN_ACTIVITY)
                    .build());
            log.debug("User {} bound to activity {} with channel {}", userId, chatId, channel.id());
        } else if ("ROOM".equals(type)) {
            // 绑定到直播间channel，使用userId作为key
            roomChannels.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>())
                    .put(userId, channel);
            
            sendToRoom(chatId, ChatMessage.builder()
                    .roomId(chatId)
                    .fromUserId(userId)
                    .fromUserName(userName)
                    .content(userName+" 加入了直播间")
                    .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                    .messageId(UUID.randomUUID().toString())
                    .type(ChatMessage.MessageType.JOIN_ROOM)
                    .build());
            log.debug("User {} bound to room {} with channel {}", userId, chatId, channel.id());
        }
    }

    @Override
    public void sendToUser(String userId, ChatMessage message) {
        try {
            // 设置发送者用户名
            if (message.getFromUserName() == null) {
                String userName = userNameCache.get(message.getFromUserId());
                if (userName != null) {
                    message.setFromUserName(userName);
                }
            }
            
            String messageJson = objectMapper.writeValueAsString(message);
            Set<Channel> channels = privateChannels.get(userId);

            minioService.handleImageMessage(message, false); // 处理私聊图片消息
            //存储私聊消息
            messageStorage.savePrivateMessage(message);
            if (channels != null) {
                channels.forEach(channel -> {
                    if (channel.isActive()) {
                        channel.writeAndFlush(new TextWebSocketFrame(messageJson));
                    }
                });
            }

//            //回显消息
//            Set<Channel> fromuserchannels = privateChannels.get(message.getFromUserId());
//            if (channels != null) {
//                fromuserchannels.forEach(channel -> {
//                    if (channel.isActive()) {
//                        channel.writeAndFlush(new TextWebSocketFrame(messageJson));
//                    }
//                });
//            }

        } catch (Exception e) {
            log.error("Error sending private message to user {}", userId, e);
        }
    }

    @Override
    public void sendToActivity(String activityId, ChatMessage message) {
        try {
            if (message.getFromUserName() == null) {
                String userName = userNameCache.get(message.getFromUserId());
                if (userName != null) {
                    message.setFromUserName(userName);
                }
            }

            String messageJson = objectMapper.writeValueAsString(message);
            ConcurrentMap<String, Channel> userChannels = activityChannels.get(activityId);

            minioService.handleImageMessage(message, true);  // 处理群聊图片消息
            messageStorage.saveActivityMessage(message);
            
            if (userChannels != null) {
                userChannels.values().forEach(channel -> {
                    if (channel.isActive()) {
                        channel.writeAndFlush(new TextWebSocketFrame(messageJson));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error sending message to activity {}", activityId, e);
        }
    }

    @Override
    public void sendToRoom(String roomId, ChatMessage message) {
        try {
            if (message.getFromUserName() == null) {
                String userName = userNameCache.get(message.getFromUserId());
                if (userName != null) {
                    message.setFromUserName(userName);
                }
            }
            
            String messageJson = objectMapper.writeValueAsString(message);
            ConcurrentMap<String, Channel> userChannels = roomChannels.get(roomId);
            
            if (userChannels != null) {
                userChannels.values().forEach(channel -> {
                    if (channel.isActive()) {
                        channel.writeAndFlush(new TextWebSocketFrame(messageJson));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error sending message to room {}", roomId, e);
        }
    }



    @Override
    public void leaveActivity(String userId) {
        cleanupActivityChannels(userId);
    }

    @Override
    public void leaveRoom(String userId) {
        cleanupRoomChannels(userId);
    }

    @Override
    public ConcurrentMap<String, Channel> getActivityChannels(String activityId) {
        return activityChannels.get(activityId);
    }

    @Override
    public ConcurrentMap<String, Channel> getRoomChannels(String roomId) {
        return roomChannels.get(roomId);
    }

    @Override
    public void unbindUserChannel(String userId) {
        // 清理私聊channels
        cleanupPrivateChannels(userId);
        
        // 清理活动channels
        cleanupActivityChannels(userId);
        
        // 清理直播间channels
        cleanupRoomChannels(userId);
        
        // 清理用户名缓存
        userNameCache.remove(userId);
    }

    private void cleanupPrivateChannels(String userId) {
        Set<Channel> userChannels = privateChannels.get(userId);
        if (userChannels != null) {
            userChannels.removeIf(channel -> !channel.isActive());
            if (userChannels.isEmpty()) {
                privateChannels.remove(userId);
            }
        }
    }

    private void cleanupActivityChannels(String userId) {
        for (Map.Entry<String, ConcurrentMap<String, Channel>> entry : activityChannels.entrySet()) {
            String activityId = entry.getKey();
            ConcurrentMap<String, Channel> userChannels = entry.getValue();
            
            Channel channel = userChannels.remove(userId);
            if (channel != null) {
                String userName = userNameCache.get(userId);
                sendToActivity(activityId, ChatMessage.builder()
                        .activityId(activityId)
                        .fromUserId(userId)
                        .fromUserName(userName)
                        .content((userName != null ? userName : userId) + " 离开了活动聊天")
                        .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                        .messageId(UUID.randomUUID().toString())
                        .type(ChatMessage.MessageType.LEAVE_ACTIVITY)
                        .build());
            }
            
            if (userChannels.isEmpty()) {
                activityChannels.remove(activityId);
            }
        }
    }

    private void cleanupRoomChannels(String userId) {
        for (Map.Entry<String, ConcurrentMap<String, Channel>> entry : roomChannels.entrySet()) {
            String roomId = entry.getKey();
            ConcurrentMap<String, Channel> userChannels = entry.getValue();
            
            Channel channel = userChannels.remove(userId);
            if (channel != null) {
                String userName = userNameCache.get(userId);
                sendToRoom(roomId, ChatMessage.builder()
                        .roomId(roomId)
                        .fromUserId(userId)
                        .fromUserName(userName)
                        .content((userName != null ? userName : userId) + " 离开了直播间")
                        .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                        .messageId(UUID.randomUUID().toString())
                        .type(ChatMessage.MessageType.LEAVE_ROOM)
                        .build());
            }
            
            if (userChannels.isEmpty()) {
                roomChannels.remove(roomId);
            }
        }
    }

    private void ensureMessageTimestamp(ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
} 