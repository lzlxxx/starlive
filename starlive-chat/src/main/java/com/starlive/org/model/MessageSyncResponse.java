package com.starlive.org.model;

import lombok.Data;

import java.util.List;

@Data
public class MessageSyncResponse {
    private List<ChatMessage> messages;  // 新消息列表
    private Long currentTimestamp;  // 当前服务器时间戳
    private Boolean hasMore;  // 是否还有更多消息
} 