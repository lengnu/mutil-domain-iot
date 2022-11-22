package com.multi.domain.iot.ud.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.ud.handler.request.AuditAgentReturnPIDRequestHandler;
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
 * @BelongsPackage: com.multi.domain.iot.ud.starter
 * @Author: duwei
 * @Date: 2022/11/21 17:25
 * @Description: UD作为服务器启动类
 */
@Slf4j
public class UDServerStarter {

    private final int listenPort;
    private final Domain domain;

    public UDServerStarter(int port, Domain domain) throws InterruptedException {
        this.listenPort = port;
        this.domain = domain;
        this.init();
    }


    private final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private final AuditAgentReturnPIDRequestHandler auditAgentReturnPIDRequestHandler = AuditAgentReturnPIDRequestHandler.INSTANCE;

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
                        nioSocketChannel.pipeline().addLast(auditAgentReturnPIDRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(listenPort).sync();
        if (channelFuture.isSuccess()) {
            log.info("UD listen in port {} successfully", listenPort);
        } else {
            log.error("UD  start in port {} error", listenPort);
        }
    }
}
