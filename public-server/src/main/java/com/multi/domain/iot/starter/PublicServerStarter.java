package com.multi.domain.iot.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.handler.EnrollInformationRequestHandler;
import com.multi.domain.iot.handler.FetchPublicParameterRequestHandler;
import com.multi.domain.iot.handler.QueryAuditAgentAndIDVerifiersRequestHandler;
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
 * @BelongsPackage: com.multi.domain.iot.server
 * @Author: duwei
 * @Date: 2022/11/17 16:51
 * @Description: 公共服务器启动类
 */
@Slf4j
public class PublicServerStarter {
    private static final int PORT = 9999;
    private  final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private  final FetchPublicParameterRequestHandler fetchPublicParameterRequestHandler = FetchPublicParameterRequestHandler.INSTANCE;
    private final EnrollInformationRequestHandler enrollInformationRequestHandler = EnrollInformationRequestHandler.INSTANCE;
    private final QueryAuditAgentAndIDVerifiersRequestHandler queryAuditAgentAndIDVerifiersRequestHandler = QueryAuditAgentAndIDVerifiersRequestHandler.INSTANCE;

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
                        nioSocketChannel.pipeline().addLast(fetchPublicParameterRequestHandler);
                        nioSocketChannel.pipeline().addLast(enrollInformationRequestHandler);
                        nioSocketChannel.pipeline().addLast(queryAuditAgentAndIDVerifiersRequestHandler);
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
        if (channelFuture.isSuccess()){
            log.info("public server start in port {} successfully" ,PORT);
        }else {
            log.error("public server start in port {} error" ,PORT);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        PublicServerStarter starter = new PublicServerStarter();
        starter.init();
    }
}
