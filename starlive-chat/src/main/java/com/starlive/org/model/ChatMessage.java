package com.starlive.org.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * 用于封装聊天相关的所有消息类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    /** 消息的唯一标识符 */
    private String messageId;

    /** 发送消息的用户ID */
    private String fromUserId;

    /** 发送消息的用户名称 */
    private String fromUserName;

    /** 接收消息的用户ID（私聊时使用） */
    private String toUserId;

    /** 聊天房间ID（群聊时使用） */
    private String roomId;

    /** 活动ID（活动聊天时使用） */
    private String activityId;

    /** 消息内容 */
    private String content;

    /** 消息类型 */
    private MessageType type;

    private String ImageData;//前端发送的图片的二进制数据

    private String imageUrl;  // 新增字段，用于存储图片 URL


    /** 消息发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
    /** 同步开始时间 */
    private String syncStartTime;
    
    /** 同步结束时间 */
    private String syncEndTime;

    private int page;

    /**
     * 消息类型枚举
     * 用于区分不同类型的消息
     */
    public enum MessageType {
        CONNECT,        // 连接消息
        PRIVATE_CHAT,   // 私聊消息
        ROOM_CHAT,      // 房间消息
        ACTIVITY_CHAT,  // 活动聊天消息
        JOIN_ROOM,      // 加入房间
        JOIN_ACTIVITY,  // 加入活动
        LEAVE_ROOM,     // 离开房间
        LEAVE_ACTIVITY, // 离开活动
        SYSTEM,         // 系统消息
        HEARTBEAT ,      // 心跳消息
        SEARCH_MESSAGE,    // 新增搜索消息类型
        SEARCH_RESULT,     // 新增搜索结果类型
        GET_HISTORY,       // 新增获取历史消息类型
        HISTORY_RESULT,     // 新增历史消息结果类型
        SYNC_MESSAGE,  // 同步消息

    }
}