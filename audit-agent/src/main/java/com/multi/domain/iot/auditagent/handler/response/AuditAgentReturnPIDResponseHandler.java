package com.multi.domain.iot.auditagent.handler.response;

import com.multi.domain.iot.common.protocol.response.AuditAgentReturnPIDResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:30
 * @description UD验证AA生成的PID之后，返回是否成功，然后AA执行下面处理
 */
@Slf4j
public class AuditAgentReturnPIDResponseHandler extends SimpleChannelInboundHandler<AuditAgentReturnPIDResponsePacket> {
   private AuditAgentReturnPIDResponseHandler(){}

    public static final AuditAgentReturnPIDResponseHandler INSTANCE = new AuditAgentReturnPIDResponseHandler();

   @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuditAgentReturnPIDResponsePacket responsePacket) throws Exception {
       byte[] identityProtectionInformation = responsePacket.getIdentityProtectionInformation();
       byte[] pid = responsePacket.getPID();
       //TODO
       System.out.println("上链了上链了......");
   }
}
