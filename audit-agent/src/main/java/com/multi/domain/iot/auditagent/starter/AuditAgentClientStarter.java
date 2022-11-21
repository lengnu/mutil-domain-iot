package com.multi.domain.iot.auditagent.starter;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.auditagent.handler.response.EnrollInformationResponseHandler;
import com.multi.domain.iot.auditagent.handler.response.FetchPublicParameterResponseHandler;
import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.request.FetchPublicParameterRequestPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.start
 * @Author: duwei
 * @Date: 2022/11/17 17:03
 * @Description: 审计代理类客户端启动器
 */
@Slf4j
public class AuditAgentClientStarter {
    private static final String PUBLIC_SERVER_HOST = "127.0.0.1";
    private static final int PUBLIC_SERVER_PORT = 9999;

    public AuditAgentClientStarter() throws InterruptedException {
        this.init();
    }

    private final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private final FetchPublicParameterResponseHandler fetchPublicParameterResponseHandler = FetchPublicParameterResponseHandler.INSTANCE;
    private final EnrollInformationResponseHandler enrollInformationResponseHandler = EnrollInformationResponseHandler.INSTANCE;

    private void init() throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        nioSocketChannel.pipeline().addLast(packetCodecHandler);
                        nioSocketChannel.pipeline().addLast(fetchPublicParameterResponseHandler);
                        nioSocketChannel.pipeline().addLast(enrollInformationResponseHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(PUBLIC_SERVER_HOST, PUBLIC_SERVER_PORT)).sync();
        if (channelFuture.isSuccess()){
            Channel channel = channelFuture.channel();
            log.info("Connect public server successfully and request public parameters");
            FetchPublicParameterRequestPacket requestPacket = new FetchPublicParameterRequestPacket();
            channel.writeAndFlush(requestPacket);
        }

    }

}
