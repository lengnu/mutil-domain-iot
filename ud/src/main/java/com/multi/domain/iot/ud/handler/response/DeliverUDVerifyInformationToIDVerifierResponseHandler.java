package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.protocol.response.UDAuthenticationMessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.handler.response
 * @Author: duwei
 * @Date: 2022/11/21 14:26
 * @Description: UD工广播其验证消息
 */
@Slf4j
public class DeliverUDVerifyInformationToIDVerifierResponseHandler extends SimpleChannelInboundHandler<UDAuthenticationMessageResponsePacket> {
    private DeliverUDVerifyInformationToIDVerifierResponseHandler() {

    }

    public static final DeliverUDVerifyInformationToIDVerifierResponseHandler INSTANCE = new DeliverUDVerifyInformationToIDVerifierResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDAuthenticationMessageResponsePacket responsePacket) throws Exception {
        System.out.println(responsePacket.getReason() + "reason");
    }
}
