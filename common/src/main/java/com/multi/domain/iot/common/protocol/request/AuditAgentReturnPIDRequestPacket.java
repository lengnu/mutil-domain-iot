package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:00
 * @description AA向UD返回PID请求报文
 */
@Data
public class AuditAgentReturnPIDRequestPacket extends Packet {
    private byte[] PID;
    private byte[] hash;
    private byte[] sign;
    private boolean success;
    private String reason;
    @Override
    public byte getMessageType() {
        return MessageType.AUDIT_AGENT_RETURN_PID_REQUEST;
    }
}
