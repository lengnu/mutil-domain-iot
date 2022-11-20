package com.multi.domain.iot.handler;

import com.multi.domain.iot.param.AuditAgentParams;
import com.multi.domain.iot.param.AuditAgentParamsFactory;
import com.multi.domain.iot.protocol.response.EnrollInformationResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-19 19:37
 * @description 注册信息响应
 */
@Slf4j
public class EnrollInformationResponseHandler extends SimpleChannelInboundHandler<EnrollInformationResponsePacket> {
   private EnrollInformationResponseHandler(){

   }

   public static final EnrollInformationResponseHandler INSTANCE = new EnrollInformationResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()){
            AuditAgentParams auditAgentParams = AuditAgentParamsFactory.getInstance();
            auditAgentParams.setId(responsePacket.getId());
            auditAgentParams.writeSelfParamsToFile(AuditAgentParamsFactory.SELF_PARAMS_SAVE_PATH);
            log.info("The auditAgent self params are already saved in the file : {} ",AuditAgentParamsFactory.SELF_PARAMS_SAVE_PATH);
            log.info("auditAgentParams : \n{}",auditAgentParams);
            log.info("Information registered successfully, current audit-agent ID : {}",responsePacket.getId());
        }else {
            log.error("A location error has occurred and is exiting......");
            System.exit(1);
        }
    }
}
