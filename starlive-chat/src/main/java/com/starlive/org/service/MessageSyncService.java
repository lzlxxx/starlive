package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息同步服务接口
 * 定义了同步私聊消息和活动消息的方法
 */
public interface MessageSyncService {

    /**
     * 同步私聊消息
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 获取消息页数
     * @return 新消息列表
     */
    List<ChatMessage> syncPrivateMessages(String userId, String targetUserId, LocalDateTime startTime, LocalDateTime endTime, int page);

    /**
     * 同步活动消息
     * @param activityId 活动ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 获取消息数量限制
     * @return 新消息列表
     */
    List<ChatMessage> syncActivityMessages(String activityId, LocalDateTime startTime, LocalDateTime endTime, int page);
}