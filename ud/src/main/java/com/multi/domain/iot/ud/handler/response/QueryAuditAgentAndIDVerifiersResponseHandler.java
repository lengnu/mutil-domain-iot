package com.multi.domain.iot.ud.handler.response;

import com.multi.domain.iot.common.protocol.request.UDDeliverVerifyInformationToIDVerifierRequestPacket;
import com.multi.domain.iot.ud.param.UDParams;
import com.multi.domain.iot.ud.param.UDParamsFactory;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.ud.pool.IDVerifierConnectionPoolingFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:59
 * @description 查询AA和IDV在服务器的注册响应处理器
 */
@Slf4j
public class QueryAuditAgentAndIDVerifiersResponseHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersResponsePacket> {
    private QueryAuditAgentAndIDVerifiersResponseHandler() {

    }

    public static final QueryAuditAgentAndIDVerifiersResponseHandler INSTANCE = new QueryAuditAgentAndIDVerifiersResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersResponsePacket responsePacket) throws Exception {
        log.info("Query the information of auditAgent and IDVerifiers in  domain {} successfully", UDParamsFactory.domain);
        log.info("Construct real identity information and calculate identity protection information");
        UDParams udParams = UDParamsFactory.getInstance();
        //存储系统中其他实体的地址，公钥等信息
        udParams.saveOtherPublicKeyAndAddress(responsePacket.getRoleInformationMap());
        //TODO 阶
        udParams.generateAllBoardCastInformation(3);

        Channel channel = IDVerifierConnectionPoolingFactory.INSTANCE.getChannel(udParams.getVerifiersConnectionsInformation().get(2));
        UDDeliverVerifyInformationToIDVerifierRequestPacket requestPacket = new UDDeliverVerifyInformationToIDVerifierRequestPacket();
        requestPacket.setUdAllVerifyInformation(udParams.getUdAllVerifyInformation());
        channel.writeAndFlush(requestPacket);
        System.out.println("wfadsad");
    }


}
