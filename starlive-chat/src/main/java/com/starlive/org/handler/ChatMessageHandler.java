package com.starlive.org.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.constant.ChatConstant;
import com.starlive.org.entity.User;
import com.starlive.org.model.ChatMessage;
import com.starlive.org.service.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.*;

@Slf4j
@Component
@Sharable
public class ChatMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");
    private static final AttributeKey<String> CHAT_TYPE_KEY = AttributeKey.valueOf("chatType");
    private static final AttributeKey<String> CHAT_ID_KEY = AttributeKey.valueOf("chatId");
    private static final AttributeKey<String> FORM_USER_NAME_KEY = AttributeKey.valueOf("formUserName");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ExecutorService executor = new ThreadPoolExecutor(
            5, 20, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private RoomChatService roomChatService;

    @Autowired
    private ActivityChatService activityChatService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private MessageSyncService messageSyncService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Channel active: {}", ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String text = frame.text();
        ChatMessage message = objectMapper.readValue(text, ChatMessage.class);

        switch (message.getType()) {
            case CONNECT->executor.execute(() ->{
                handleConnect(message, ctx.channel());
            });
            case SYNC_MESSAGE->executor.execute(() -> {
                handleMessageSync(message, ctx.channel());
            });

            case PRIVATE_CHAT -> executor.execute(() ->{
                handlePrivateChat(message);
            });

            case ROOM_CHAT -> executor.execute(() ->{
                handleRoomChat(message);
            });
            case ACTIVITY_CHAT -> executor.execute(() ->{
                handleActivityChat(message);
            });
            case LEAVE_ROOM -> handleLeaveRoom(message);
            case LEAVE_ACTIVITY -> handleLeaveActivity(message);
            case HEARTBEAT -> handleHeartbeat(ctx);
            default -> log.warn("Unknown message type: {}", message.getType());
        }
    }

    private void handleHeartbeat(ChannelHandlerContext ctx) {
        // 心跳消息只需要更新最后活动时间，不需要响应
        // 如果需要响应，可以发送一个简单的确认消息
        try {
            ChatMessage heartbeatResponse = new ChatMessage();
            heartbeatResponse.setType(ChatMessage.MessageType.HEARTBEAT);
            heartbeatResponse.setTimestamp(String.valueOf(LocalDateTime.now()));
            String responseJson = objectMapper.writeValueAsString(heartbeatResponse);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
        } catch (Exception e) {
            log.error("Error sending heartbeat response", e);
        }
    }

    private void handleConnect(ChatMessage message, Channel channel) {
        if (message.getFromUserId() == null || message.getFromUserId().isEmpty()) {
            log.warn("Connect message missing user ID");
            return;
        }
        if (message.getActivityId() != null) {
            // 活动聊天
            channel.attr(CHAT_TYPE_KEY).set("ACTIVITY");
            channel.attr(CHAT_ID_KEY).set(message.getActivityId());
        } else if (message.getRoomId() != null) {
            // 房间聊天
            channel.attr(CHAT_TYPE_KEY).set("ROOM");
            channel.attr(CHAT_ID_KEY).set(message.getRoomId());
        } else {
            // 私聊
            channel.attr(CHAT_TYPE_KEY).set("PRIVATE");
            channel.attr(CHAT_ID_KEY).set(message.getFromUserId());
        }

        messageSender.bindUserChannel(message.getFromUserId(), channel);

        // 如果包含同步时间范围，进行消息同步
        if (message.getSyncStartTime() != null && message.getSyncEndTime() != null) {
            handleMessageSync(message, channel);
        }
    }

    public void handlePrivateChat(ChatMessage message) {
        if (message.getToUserId() == null || message.getToUserId().isEmpty()) {
            log.warn("Private chat message missing target user ID");
            return;
        }

        log.debug("Sending private message from {} to {}",
                message.getFromUserId(), message.getToUserId());

        privateChatService.sendMessage(message);
    }

    public void handleRoomChat(ChatMessage message) {
        log.debug("Handling room chat message. RoomId: {}, FromUser: {}",
                message.getRoomId(), message.getFromUserId());
        roomChatService.sendMessage(message);
    }

    public void handleLeaveRoom(ChatMessage message) {
        if(message.getFromUserId() == null || message.getRoomId() == null) {
            log.warn("Invalid leave room message: missing required fields");
            return;
        }
        roomChatService.leaveRoom(message.getFromUserId(), message.getRoomId());

    }

    private void handleActivityChat(ChatMessage message) {
        if (message.getActivityId() == null || message.getFromUserId() == null) {
            log.warn("Invalid activity chat message: missing required fields");
            return;
        }

        try {
            activityChatService.sendActivityMessage(message);
            log.debug("Activity chat message processed: activityId={}, fromUser={}",
                    message.getActivityId(), message.getFromUserId());
        } catch (Exception e) {
            log.error("Error handling activity chat message", e);
        }
    }


    private void handleLeaveActivity(ChatMessage message) {
        String userId = message.getFromUserId();
        String activityId = message.getActivityId();

        if (userId == null || activityId == null) {
            log.warn("Invalid leave activity message: missing required fields");
            return;
        }

        try {
            activityChatService.leaveActivity(userId, activityId);

            log.debug("User {} left activity {}", userId, activityId);
        } catch (Exception e) {
            log.error("Error handling leave activity", e);
        }
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.READER_IDLE) {
                log.warn("Reader idle timeout, closing channel: {}", ctx.channel().id());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String userId = ctx.channel().attr(USER_ID_KEY).get();
        if (userId != null) {
            messageSender.unbindUserChannel(userId);
            // 处理用户断开连接时的清理工作
            //handleUserDisconnect(userId);
        }
    }

//    private void handleUserDisconnect(String userId) {
//        try {
//            // 查找用户所在的所有活动并处理离开
//            for (String activityId : activityChatService.getActivityOnlineUsers(userId)) {
//                activityChatService.leaveActivity(userId, activityId);
//            }
//        } catch (Exception e) {
//            log.error("Error handling user disconnect for activities", e);
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel exception caught", cause);
        ctx.close();
    }

    private void sendErrorMessage(ChannelHandlerContext ctx, String errorMessage) {
        try {
            ChatMessage errorMsg = new ChatMessage();
            errorMsg.setType(ChatMessage.MessageType.SYSTEM);
            errorMsg.setContent(errorMessage);
            errorMsg.setTimestamp(String.valueOf(LocalDateTime.now()));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(errorMsg)));
        } catch (Exception e) {
            log.error("Error sending error message", e);
        }
    }

    private void handleMessageSync(ChatMessage message, Channel channel) {
        try {
            // 使用新的格式解析时间
            LocalDateTime startTime = LocalDateTime.parse(message.getSyncStartTime(), DATE_TIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(message.getSyncEndTime(), DATE_TIME_FORMATTER);
            int page = message.getPage();

            // 如果是活动消息，同步活动消息
            if (message.getActivityId() != null) {
                String activityId = message.getActivityId();
                List<ChatMessage> activityMessages = messageSyncService.syncActivityMessages(
                        activityId, startTime, endTime, page
                );
                for(ChatMessage activityMessage:activityMessages){
                    String responseJson = objectMapper.writeValueAsString(activityMessage);
                    channel.writeAndFlush(new TextWebSocketFrame(responseJson));
                }
            } else {
                String userId = message.getFromUserId();
                String targetUserId = message.getToUserId();
                // 同步私聊消息
                List<ChatMessage> privateMessages = messageSyncService.syncPrivateMessages(
                        userId, targetUserId, startTime, endTime, page
                );

                for(ChatMessage privateMessage:privateMessages){
                    String responseJson = objectMapper.writeValueAsString(privateMessage);
                    channel.writeAndFlush(new TextWebSocketFrame(responseJson));
                }

                log.debug("Message sync completed for user: {}, time range: {} to {}",
                        userId, startTime, endTime);
            }
        } catch (Exception e) {
            log.error("Error syncing messages", e);
        }
    }
}