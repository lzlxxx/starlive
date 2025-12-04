package com.starlive.org.service.impl;

import com.starlive.org.model.ChatMessage;
import com.starlive.org.model.ChatMessageVo;
import com.starlive.org.service.MessageSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageSyncServiceImpl implements MessageSyncService {

    @Autowired
    private MessageFileStorage messageStorage;

    @Override
    public List<ChatMessage> syncPrivateMessages(String userId, String targetUserId, LocalDateTime startTime, LocalDateTime endTime, int page) {

        
        // 获取用户的私聊消息
        ChatMessageVo messages = messageStorage.findPrivateMessagesByTimeRange(userId, targetUserId,startTime,endTime,page);
        return messages.getMessages();

    }

    @Override
    public List<ChatMessage> syncActivityMessages(String activityId, LocalDateTime startTime, LocalDateTime endTime, int page) {
        
        // 获取活动的所有消息
        ChatMessageVo messages = messageStorage.findActivityMessagesByTimeRange(activityId, startTime,endTime,page);
        return messages.getMessages();
    }
}