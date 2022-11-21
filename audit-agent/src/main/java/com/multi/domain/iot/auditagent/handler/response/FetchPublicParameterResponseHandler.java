package com.multi.domain.iot.auditagent.handler.response;

import com.multi.domain.iot.auditagent.param.AuditAgentParams;
import com.multi.domain.iot.auditagent.param.AuditAgentParamsFactory;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.common.role.Role;
import com.multi.domain.iot.common.util.IPUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-17 23:03
 * @description 拉去公共参数响应Handler
 */
@Slf4j
public class FetchPublicParameterResponseHandler extends SimpleChannelInboundHandler<FetchPublicParameterResponsePacket> {
    private FetchPublicParameterResponseHandler(){

    }

    public static final FetchPublicParameterResponseHandler INSTANCE = new FetchPublicParameterResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()){
            log.info("Get server public params successfully");
            AuditAgentParams auditAgentParams = AuditAgentParamsFactory.getInstance(responsePacket.getPublicParams());
            log.info("The auditAgent public params are already saved in the file : {} ",AuditAgentParamsFactory.PUBLIC_PARAMS_SAVE_PATH);
            //将自身信息注册到服务器
            log.info("The auditAgent begins to register its public key and address information with the public server");
            ctx.writeAndFlush(wrapEnrollInformation(auditAgentParams));
        }else {
            log.error("unknown error,exit......");
            System.exit(1);
        }
    }

    public Packet wrapEnrollInformation(AuditAgentParams auditAgentParams){
        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
        requestPacket.setPublicKey(auditAgentParams.getPublicKey().toBytes());
        requestPacket.setRole(Role.AA);
        requestPacket.setListenPort(AuditAgentParamsFactory.listenPort);
        requestPacket.setHost(AuditAgentParamsFactory.HOST);
        return requestPacket;
    }
}
