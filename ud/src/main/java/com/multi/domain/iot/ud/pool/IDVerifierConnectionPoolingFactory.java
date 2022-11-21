package com.multi.domain.iot.ud.pool;

import com.multi.domain.iot.common.codec.PacketCodecHandler;
import com.multi.domain.iot.common.pool.ConnectionPoolingFactory;
import com.multi.domain.iot.ud.handler.response.UDDeliverVerifyInformationToIDVerifierResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:19
 * @Description: ID验证者的连接池
 */
public class IDVerifierConnectionPoolingFactory extends ConnectionPoolingFactory {

    private IDVerifierConnectionPoolingFactory(){}

    public static final IDVerifierConnectionPoolingFactory INSTANCE = new IDVerifierConnectionPoolingFactory();

    @Override
    protected Channel doCreateChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        final PacketCodecHandler packetCodecHandler = PacketCodecHandler.INSTANCE;
        final UDDeliverVerifyInformationToIDVerifierResponseHandler uDDeliverVerifyInformationToIDVerifierResponseHandler = UDDeliverVerifyInformationToIDVerifierResponseHandler.INSTANCE;
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
                        nioSocketChannel.pipeline().addLast(uDDeliverVerifyInformationToIDVerifierResponseHandler);

                    }
                });
        return bootstrap.connect(new InetSocketAddress(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort())).sync().channel();
    }
}
