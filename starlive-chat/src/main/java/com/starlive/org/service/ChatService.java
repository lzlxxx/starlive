package com.starlive.org.service;

import com.starlive.org.model.ChatMessage;
import java.util.List;

/**
 * 聊天服务接口
 * 定义了聊天系统的核心业务功能
 */
public interface ChatService {
    /**
     * 获取活动群聊在线用户数量
     * @param activityId
     * @return
     */
    int getActivityUserCount(String activityId);
    /**
     * 获取房间当前在线用户数
     * @param roomId 房间ID
     * @return 在线用户数量
     */
    int getRoomOnlineCount(String roomId);

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return true表示用户在线，false表示用户离线
     */
    boolean isUserOnline(String userId);
}