package com.multi.domain.iot.auditagent.pool;

import com.multi.domain.iot.auditagent.handler.response.AuditAgentReturnPIDResponseHandler;
import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.pool.ConnectionPoolingFactory;
import com.multi.domain.iot.common.protocol.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:27
 * @description UD连接池工厂
 */
public class UDConnectionPoolingFactory extends ConnectionPoolingFactory {
    private UDConnectionPoolingFactory(){}
    public static final UDConnectionPoolingFactory INSTANCE = new UDConnectionPoolingFactory();

    @Override
    protected void boardCast(Packet packet, Collection<InetSocketAddress> connections) throws InterruptedException {

    }

    @Override
    protected Channel doCreateChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
        final AuditAgentReturnPIDResponseHandler auditAgentReturnPIDResponseHandler = AuditAgentReturnPIDResponseHandler.INSTANCE;
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
                        nioSocketChannel.pipeline().addLast(auditAgentReturnPIDResponseHandler);
                    }
                });
        Channel channel =  bootstrap.connect(new InetSocketAddress(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort())).sync().channel();
        return channel;
    }
}
