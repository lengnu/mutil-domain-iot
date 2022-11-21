package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.verifier.param.IDVerifierParams;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.protocol.response.FetchPublicParameterResponsePacket;
import com.multi.domain.iot.common.role.Role;
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
            log.info("The id-Verifier public params are already saved in the file : {} ",IDVerifierParamsFactory.PUBLIC_PARAMS_SAVE_PATH);
            //将自身信息注册到服务器
            log.info("The id-Verifier begins to register its public key and address information with the server");
            ctx.writeAndFlush(wrapEnrollInformation(idVerifierParams));
        }else {
            log.error("unknown error,exit......");
            System.exit(1);
        }
    }

    public Packet wrapEnrollInformation(IDVerifierParams idVerifierParams){
        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
        requestPacket.setPublicKey(idVerifierParams.getPublicKey().toBytes());
        requestPacket.setRole(Role.IDV);
        requestPacket.setListenPort(IDVerifierParamsFactory.listenPort);
        requestPacket.setHost(IDVerifierParamsFactory.HOST);
        requestPacket.setDomain(IDVerifierParamsFactory.domain);
        return requestPacket;
    }
}
