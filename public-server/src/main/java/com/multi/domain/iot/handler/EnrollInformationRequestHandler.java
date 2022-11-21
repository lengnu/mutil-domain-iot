package com.multi.domain.iot.handler;

import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
import com.multi.domain.iot.session.SessionUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/18 15:57
 * @Description: 处理各个客户端的注册信息
 */
@Slf4j
@ChannelHandler.Sharable
public class EnrollInformationRequestHandler extends SimpleChannelInboundHandler<EnrollInformationRequestPacket> {
    private EnrollInformationRequestHandler(){

    }

    public static final EnrollInformationRequestHandler INSTANCE = new EnrollInformationRequestHandler();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationRequestPacket requestPacket) throws Exception {
        int id = SessionUtils.bindSession(requestPacket);
        EnrollInformationResponsePacket packet = new EnrollInformationResponsePacket();
        if (id < 0){
            packet.setSuccess(false);
            packet.setReason("AuditAgent cannot be registered repeatedly");
        }else {
            packet.setId(id);
            packet.setSuccess(true);
        }
        log.info("Listen to {} registration",requestPacket.getRole().getRole());
        ctx.writeAndFlush(packet);
    }

}
