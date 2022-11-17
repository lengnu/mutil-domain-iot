package com.multi.domain.iot.handler;

import com.multi.domain.iot.param.AuditAgentParams;
import com.multi.domain.iot.param.AuditAgentParamsFactory;
import com.multi.domain.iot.param.PublicParams;
import com.multi.domain.iot.protocol.response.FetchPublicParameterResponsePacket;
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
            log.info("Get server common parameters successfully...");
            PublicParams publicParams = responsePacket.getPublicParams();
            AuditAgentParams auditAgentParams = AuditAgentParamsFactory.getInstance(publicParams);
            log.info("The auditAgentParams are already saved in the file : {} ",AuditAgentParamsFactory.PARAMS_SAVE_PATH);
            log.info("auditAgentParams : {}",auditAgentParams);
        }else {
            log.error("unknown error,exit......");
            System.exit(1);
        }
    }
}
