package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:00
 * @description AA向UD返回PID请求报文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditAgentReturnPIDResponsePacket extends Packet {
    private byte[] identityProtectionInformation;
    private byte[] PID;
    @Override
    public byte getMessageType() {
        return MessageType.AUDIT_AGENT_RETURN_PID_RESPONSE;
    }
}
