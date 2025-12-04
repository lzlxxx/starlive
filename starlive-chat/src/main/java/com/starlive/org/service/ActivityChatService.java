package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;
import java.util.List;

/**
 * 活动聊天服务接口
 * 定义了活动聊天相关的业务功能
 */
public interface ActivityChatService {

    /**
     * 离开活动聊天
     * @param userId 用户ID
     * @param activityId 活动ID
     * @throws IllegalArgumentException 当用户ID或活动ID无效时抛出
     */
    void leaveActivity(String userId, String activityId);

    /**
     * 发送活动聊天消息
     * @param message 消息对象，必须包含发送者ID和活动ID
     * @throws IllegalArgumentException 当消息对象无效时抛出
     */
    void sendActivityMessage(ChatMessage message);


} 