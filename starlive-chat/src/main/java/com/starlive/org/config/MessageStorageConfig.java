package com.starlive.org.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "chat.storage")
public class MessageStorageConfig {
    
    /** 消息存储根目录 */
    private String path = "./chat_messages";
    
    /** 单个文件最大大小（字节） */
    private long maxFileSize = 10 * 1024 * 1024; // 默认10MB
    
    /** 消息保留天数 */
    private int retentionDays = 7;
    
    /** 单个聊天记录文件最大消息数 */
    private int maxMessagesPerFile = 1000;
} 