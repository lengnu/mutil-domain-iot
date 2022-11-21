package com.multi.domain.iot.verifier.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.verifier.handler.request.UDDeliverVerifyInformationToIDVerifierRequestHandler;
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
 * @BelongsPackage: com.multi.domain.iot.verifier.starter
 * @Author: duwei
 * @Date: 2022/11/21 14:10
 * @Description: 验证者作为服务器启动类
 */
@Slf4j
public class IDVerifierServerStarter {
    private final int listenPort;
    private final Domain domain;

    public IDVerifierServerStarter(int port, Domain domain) throws InterruptedException {
        this.listenPort = port;
        this.domain = domain;
        this.init();
    }


    private final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private final UDDeliverVerifyInformationToIDVerifierRequestHandler udDeliverVerifyInformationToIDVerifierRequestHandler = UDDeliverVerifyInformationToIDVerifierRequestHandler.INSTANCE;

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
                        nioSocketChannel.pipeline().addLast(udDeliverVerifyInformationToIDVerifierRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(listenPort).sync();
        if (channelFuture.isSuccess()) {
            log.info("id-Verifier listen in port {} successfully", listenPort);
        } else {
            log.error("id-Verifier  start in port {} error", listenPort);
        }
    }
}
