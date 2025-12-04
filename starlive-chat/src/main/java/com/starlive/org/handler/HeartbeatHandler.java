package com.starlive.org.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳检测处理器
 * 用于检测客户端连接是否存活
 */
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    /** 允许的最大连续心跳丢失次数 */
    private static final int MAX_MISSED_HEARTBEATS = 3;

    /** 当前已丢失的心跳次数 */
    private int missedHeartbeats = 0;

    /**
     * 处理空闲状态事件
     * 当连接空闲时间超过配置时间时触发
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                missedHeartbeats++;
                log.warn("Missed heartbeat #{} for channel: {}",
                        missedHeartbeats, ctx.channel().remoteAddress());

                // 超过最大丢失次数，关闭连接
                if (missedHeartbeats >= MAX_MISSED_HEARTBEATS) {
                    log.warn("Channel inactive due to missing heartbeats: {}",
                            ctx.channel().remoteAddress());
                    ctx.close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 处理接收到的消息
     * 收到任何消息都重置心跳计数
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        missedHeartbeats = 0;
        super.channelRead(ctx, msg);
    }
}