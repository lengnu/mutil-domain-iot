package com.multi.domain.iot.starter;

import com.multi.domain.iot.codec.PacketCodecHandler;
import com.multi.domain.iot.protocol.request.FetchPublicParameterRequestPacket;
import com.multi.domain.iot.handler.EnrollInformationResponseHandler;
import com.multi.domain.iot.handler.FetchPublicParameterResponseHandler;
import com.multi.domain.iot.handler.QueryAuditAgentAndIDVerifiersResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.start
 * @Author: duwei
 * @Date: 2022/11/17 17:03
 * @Description: 通信设备UD类启动器
 */
public class UDStarter {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9999;

    private final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
    private final FetchPublicParameterResponseHandler fetchPublicParameterResponseHandler = FetchPublicParameterResponseHandler.INSTANCE;
    private final EnrollInformationResponseHandler enrollInformationResponseHandler = EnrollInformationResponseHandler.INSTANCE;
    private final QueryAuditAgentAndIDVerifiersResponseHandler queryAuditAgentAndIDVerifiersResponseHandler = QueryAuditAgentAndIDVerifiersResponseHandler.INSTANCE;

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
                        nioSocketChannel.pipeline().addLast(queryAuditAgentAndIDVerifiersResponseHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(HOST, PORT)).sync();
        if (channelFuture.isSuccess()) {
            Channel channel = channelFuture.channel();
            FetchPublicParameterRequestPacket requestPacket = new FetchPublicParameterRequestPacket();
            channel.writeAndFlush(requestPacket);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        UDStarter starter = new UDStarter();
        starter.init();
    }
}