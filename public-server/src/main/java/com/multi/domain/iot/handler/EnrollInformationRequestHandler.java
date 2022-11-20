package com.multi.domain.iot.handler;

import com.multi.domain.iot.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.protocol.response.EnrollInformationResponsePacket;
import com.multi.domain.iot.role.Role;
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
        int id = bindSession(requestPacket);
        log.info("Listen to {} registration,id : {}",requestPacket.getRole().getRole(),id);
        EnrollInformationResponsePacket packet = new EnrollInformationResponsePacket();
        packet.setId(id);
        packet.setSuccess(true);
        ctx.writeAndFlush(packet);
    }

    public int bindSession(EnrollInformationRequestPacket requestPacket){
        String host = requestPacket.getHost();
        int listenPort = requestPacket.getListenPort();
        byte[] publicKey = requestPacket.getPublicKey();
        Role role = requestPacket.getRole();
        return SessionUtils.bindSession(role,host,listenPort,publicKey);
    }
}
