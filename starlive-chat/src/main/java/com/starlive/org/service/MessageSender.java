package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface MessageSender {
    void sendToUser(String userId, ChatMessage message);
    void sendToRoom(String roomId, ChatMessage message);
    void bindUserChannel(String userId, Channel channel);
    void unbindUserChannel(String userId);
    void sendToActivity(String activityId, ChatMessage message);

    void leaveActivity(String userId);
    void leaveRoom(String userId);

    ConcurrentMap<String, Channel> getActivityChannels(String activityId);
    ConcurrentMap<String, Channel> getRoomChannels(String roomId);
} 