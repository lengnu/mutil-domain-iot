package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.protocol.response.UDDeliverVerifyInformationToIDVerifierResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.handler.response
 * @Author: duwei
 * @Date: 2022/11/21 14:26
 * @Description: TODO
 */
@Slf4j
public class UDDeliverVerifyInformationToIDVerifierResponseHandler extends SimpleChannelInboundHandler<UDDeliverVerifyInformationToIDVerifierResponsePacket> {
    private UDDeliverVerifyInformationToIDVerifierResponseHandler() {

    }

    public static final UDDeliverVerifyInformationToIDVerifierResponseHandler INSTANCE = new UDDeliverVerifyInformationToIDVerifierResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDDeliverVerifyInformationToIDVerifierResponsePacket responsePacket) throws Exception {
        System.out.println(responsePacket.getReason() + "reason");
    }
}
