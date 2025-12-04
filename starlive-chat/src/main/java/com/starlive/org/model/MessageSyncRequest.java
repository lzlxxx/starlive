package com.starlive.org.model;

import lombok.Data;

@Data
public class MessageSyncRequest {
    private String userId;  // 用户ID
    private String deviceId;  // 设备ID
    private Long lastSyncTimestamp;  // 最后同步时间戳
    private Integer limit;  // 每次同步的消息数量限制
} 