package com.starlive.org.constant;

/**
 * 聊天系统常量定义
 */
public class ChatConstant {

    /** 消息最大长度 */
    public static final int MAX_MESSAGE_LENGTH = 1000;

    /** 房间最大人数 */
    public static final int MAX_ROOM_MEMBERS = 1000;

    /** 历史消息默认获取条数 */
    public static final int DEFAULT_HISTORY_SIZE = 20;

    /** 心跳超时时间（秒） */
    public static final int HEARTBEAT_TIMEOUT = 60;

    /** 系统用户ID */
    public static final String SYSTEM_USER_ID = "SYSTEM";

    /** 系统用户名 */
    public static final String SYSTEM_USER_NAME = "系统消息";

    /** 默认消息同步条数 */
    public static final int DEFAULT_SYNC_LIMIT = 100;
    private ChatConstant() {
        // 防止实例化
    }
}