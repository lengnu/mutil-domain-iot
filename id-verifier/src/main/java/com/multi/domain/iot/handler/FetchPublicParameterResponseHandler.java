package com.multi.domain.iot.handler;

import com.multi.domain.iot.param.IDVerifierParams;
import com.multi.domain.iot.param.IDVerifierParamsFactory;
import com.multi.domain.iot.param.PublicParams;
import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.role.Role;
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
            IDVerifierParams idVerifierParams = IDVerifierParamsFactory.getInstance(responsePacket.getPublicParams());
            log.info("The idVerifier public params are already saved in the file : {} ",IDVerifierParamsFactory.PUBLIC_PARAMS_SAVE_PATH);
            //将自身信息注册到服务器
            log.info("The idVerifier begins to register its public key and address information with the server......");
            ctx.writeAndFlush(wrapEnrollInformation(idVerifierParams));
        }else {
            log.error("unknown error,exit......");
            System.exit(1);
        }
    }

    public Packet wrapEnrollInformation(IDVerifierParams auditAgentParams){
        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
        requestPacket.setPublicKey(auditAgentParams.getPublicKey().toBytes());
        requestPacket.setRole(Role.IDV);
        requestPacket.setListenPort(222);
        requestPacket.setHost("12");
        return requestPacket;
    }
}
