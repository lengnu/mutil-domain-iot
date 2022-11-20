package com.multi.domain.iot.handler;

import com.multi.domain.iot.param.UDParams;
import com.multi.domain.iot.param.UDParamsFactory;
import com.multi.domain.iot.protocol.request.QueryAuditAgentAndIDVerifiersRequestPacket;
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
            UDParams udParams = UDParamsFactory.getInstance(responsePacket.getPublicParams());
            log.info("The ud public params are already saved in the file : {} ",UDParamsFactory.PUBLIC_PARAMS_SAVE_PATH);
            //将自身信息注册到服务器
           log.info("Query the information of auditAgent and IDVerifiers in the system to prepare for authentication......");
            ctx.writeAndFlush(new QueryAuditAgentAndIDVerifiersRequestPacket());
        }else {
            log.error("unknown error,exit......");
            System.exit(1);
        }
    }

//    public Packet wrapEnrollInformation(UDParams udParams){
//        EnrollInformationRequestPacket requestPacket = new EnrollInformationRequestPacket();
//        requestPacket.setRole(Role.UD);
//        requestPacket.setListenPort(222);
//        requestPacket.setHost("12");
//        return requestPacket;
//    }
}
