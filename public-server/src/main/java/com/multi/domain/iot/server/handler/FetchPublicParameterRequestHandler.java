package com.multi.domain.iot.server.handler;

import com.multi.domain.iot.common.param.PublicParamsFactory;
import com.multi.domain.iot.common.protocol.request.FetchPublicParameterRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/17 16:45
 * @Description: 客户请求公共参数处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class FetchPublicParameterRequestHandler extends SimpleChannelInboundHandler<FetchPublicParameterRequestPacket> {
    private FetchPublicParameterRequestHandler() {

    }

    public static final FetchPublicParameterRequestHandler INSTANCE = new FetchPublicParameterRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterRequestPacket requestPacket) throws Exception {
        FetchPublicParameterResponsePacket responsePacket = new FetchPublicParameterResponsePacket();
        responsePacket.setPublicParams(PublicParamsFactory.getInstance());
        responsePacket.setSuccess(true);
        ctx.writeAndFlush(responsePacket);
    }
}
