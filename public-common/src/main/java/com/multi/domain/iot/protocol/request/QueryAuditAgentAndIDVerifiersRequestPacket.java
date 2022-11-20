package com.multi.domain.iot.protocol.request;

import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:37
 * @description 查询AA和所有IDV的信息请求
 */
@Data
public class QueryAuditAgentAndIDVerifiersRequestPacket extends Packet {
    @Override
    public byte getMessageType() {
        return MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_REQUEST;
    }
}
