package com.multi.domain.iot.server;

import com.multi.domain.iot.codec.PacketCodecHandler;
import com.multi.domain.iot.handler.FetchPublicParameterRequestHandler;
import com.multi.domain.iot.param.PublicParams;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.server
 * @Author: duwei
 * @Date: 2022/11/17 16:51
 * @Description: 公共服务器启动类
 */
@Slf4j
public class PublicServerStarter {
    private static final int PORT = 8888;
    private static final PacketCodecHandler PACKET_CODEC_HANDLER = PacketCodecHandler.INSTANCE;
    private static final FetchPublicParameterRequestHandler FETCH_PUBLIC_PARAMETER_REQUEST_HANDLER = FetchPublicParameterRequestHandler.INSTANCE;

    public static void main(String[] args) throws InterruptedException {
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
                            nioSocketChannel.pipeline().addLast(PACKET_CODEC_HANDLER);
                            nioSocketChannel.pipeline().addLast(FETCH_PUBLIC_PARAMETER_REQUEST_HANDLER);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
        if (channelFuture.isSuccess()){
            log.info("public server start in port {} successfully" ,PORT);
            System.out.println();
        }else {
            log.error("public server start in port {} error" ,PORT);
        }
    }
}
