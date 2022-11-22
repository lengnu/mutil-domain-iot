package com.multi.domain.iot.auditagent.starter;

import com.multi.domain.iot.auditagent.handler.request.ConfirmAuthenticationMessageRequestHandler;
import com.multi.domain.iot.common.codec.PacketCodecHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.starter
 * @Author: duwei
 * @Date: 2022/11/21 15:36
 * @Description: 审计代理类服务器启动类
 */
@Slf4j
public class AuditAgentServerStarter {
    private final int listenPort;
    private final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private final ConfirmAuthenticationMessageRequestHandler confirmAuthenticationMessageRequestHandler = ConfirmAuthenticationMessageRequestHandler.INSTANCE;

    public AuditAgentServerStarter(int port) throws InterruptedException {
        this.listenPort = port;
        this.init();
    }

    public void init() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(confirmAuthenticationMessageRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(listenPort).sync();
        if (channelFuture.isSuccess()) {
            log.info("auditAgent listen in port {} successfully", this.listenPort);
        } else {
            log.error("auditAgent start in port {} error", this.listenPort);
        }
    }
}
