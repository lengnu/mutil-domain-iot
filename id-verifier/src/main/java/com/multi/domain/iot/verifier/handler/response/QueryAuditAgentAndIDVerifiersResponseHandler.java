package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.handler.response
 * @Author: duwei
 * @Date: 2022/11/22 17:34
 * @Description: 查询AuditAgent的IP
 */
public class QueryAuditAgentAndIDVerifiersResponseHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersResponsePacket> {
   private QueryAuditAgentAndIDVerifiersResponseHandler(){

   }

   public static final QueryAuditAgentAndIDVerifiersResponseHandler INSTANCE = new QueryAuditAgentAndIDVerifiersResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersResponsePacket responsePacket) throws Exception {

    }
}
