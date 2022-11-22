package com.multi.domain.iot.handler;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.request.QueryAuditAgentAndIDVerifiersRequestPacket;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.session.SessionUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:54
 * @description 查询AA和IDV在服务器的注册的信息
 */
@ChannelHandler.Sharable
@Slf4j
public class QueryAuditAgentAndIDVerifiersRequestHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersRequestPacket> {
    private QueryAuditAgentAndIDVerifiersRequestHandler() {

    }

    public static final QueryAuditAgentAndIDVerifiersRequestHandler INSTANCE = new QueryAuditAgentAndIDVerifiersRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersRequestPacket requestPacket) throws Exception {
        Domain domain = requestPacket.getDomain();
        log.info("Detect UD query for verifiers' information in domain {}",requestPacket.getDomain().getDomain());
        QueryAuditAgentAndIDVerifiersResponsePacket responsePacket = new QueryAuditAgentAndIDVerifiersResponsePacket();
        if (SessionUtils.containsDomain(domain)){
            responsePacket.setSuccess(true);
            responsePacket.setAuditAgentSession(SessionUtils.getAuditAgentSession());
            responsePacket.setIdVerifierSession(SessionUtils.getIdVerifiersSessions(domain));
        }else {
            responsePacket.setSuccess(false);
            responsePacket.setReason("domain : " + domain.getDomain() + " not exists");
        }
        ctx.writeAndFlush(responsePacket);
    }
}
