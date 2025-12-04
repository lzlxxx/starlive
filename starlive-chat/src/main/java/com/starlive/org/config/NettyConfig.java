package com.starlive.org.config;

import com.starlive.org.handler.ChatMessageHandler;
import com.starlive.org.handler.HeartbeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket服务器配置类
 */
@Slf4j
@Component
public class NettyConfig {

    @Value("${netty.port}")
    private int port;

    @Value("${netty.websocket.path}")
    private String websocketPath;

    @Value("${netty.heartbeat.reader-idle-time:30}")
    private int readerIdleTime;

    @Value("${netty.heartbeat.writer-idle-time:30}")
    private int writerIdleTime;

    @Value("${netty.heartbeat.all-idle-time:60}")
    private int allIdleTime;

    @Autowired
    private ChatMessageHandler chatMessageHandler;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel serverChannel;

    public NettyConfig() {
        // 配置线程组
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
    }



    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        // 使用新线程启动Netty服务器，避免阻塞Spring启动
        new Thread(this::startNettyServer, "netty-server-thread").start();
    }
    private void startNettyServer() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // HTTP编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 支持大数据流
                            pipeline.addLast(new ChunkedWriteHandler());
                            // HTTP消息聚合
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            // WebSocket协议处理
                            pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true, 65536));
                            // 空闲连接检测
                            pipeline.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartbeatHandler());
                            // 自定义消息处理器
                            pipeline.addLast(chatMessageHandler);
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("Netty WebSocket server started on port: {}", port);
            } else {
                log.error("Netty server bind failed");
            }

            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            log.error("Netty server start failed: {}", e.getMessage(), e);
            shutdown();
        }
    }


    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Netty WebSocket server...");
        try {
            if (serverChannel != null && serverChannel.isActive()) {
                serverChannel.close().sync();
            }

            if (!bossGroup.isShuttingDown()) {
                bossGroup.shutdownGracefully().sync();
            }
            if (!workerGroup.isShuttingDown()) {
                workerGroup.shutdownGracefully().sync();
            }

            log.info("Netty WebSocket server shutdown completed");
        } catch (InterruptedException e) {
            log.error("Error while shutting down Netty server", e);
            Thread.currentThread().interrupt();
        }
    }

}