package com.starlive.org.controller;

import com.starlive.org.model.ChatMessage;
import com.starlive.org.result.WebResult;
import com.starlive.org.result.WebResultUtil;
import com.starlive.org.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天系统REST API控制器
 * 提供HTTP接口用于发送消息和获取历史记录
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 获取活动群聊在线人数
     * @param activityId 活动ID
     * @return 在线人数
     */
    @GetMapping("/activity/{activityId}/count")
    public WebResult<Integer> getActivityOnlineCount(@PathVariable String activityId) {
        try {
            int count = chatService.getActivityUserCount(activityId);
            log.info("activity {} has {} online users", activityId, count);
            return WebResultUtil.success(count);
        } catch (Exception e) {
            log.error("Error getting activity online count", e);
            return WebResultUtil.failure("500", "Error getting activity online count");
        }
    }

    /**
     * 获取房间在线人数
     * @param roomId 房间ID
     * @return 在线人数
     */
    @GetMapping("/room/{roomId}/count")
    public WebResult<Integer> getRoomOnlineCount(@PathVariable String roomId) {
        try {
            int count = chatService.getRoomOnlineCount(roomId);
            log.info("Room {} has {} online users", roomId, count);
            return WebResultUtil.success(count);
        } catch (Exception e) {
            log.error("Error getting room online count", e);
            return WebResultUtil.failure("500", "Error getting room online count");
        }
    }

    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return true表示在线，false表示离线
     */
    @GetMapping("/user/{userId}/online")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String userId) {
        try {
            boolean isOnline = chatService.isUserOnline(userId);
            log.info("User {} is {}", userId, isOnline ? "online" : "offline");
            return ResponseEntity.ok(isOnline);
        } catch (Exception e) {
            log.error("Error checking user online status", e);
            return ResponseEntity.badRequest().build();
        }
    }
}