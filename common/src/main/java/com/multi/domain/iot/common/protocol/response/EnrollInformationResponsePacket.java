package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import com.multi.domain.iot.common.session.Session;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.response
 * @Author: duwei
 * @Date: 2022/11/18 16:00
 * @Description: 注册信息响应包
 */
@Data
public class EnrollInformationResponsePacket extends Packet {
    private boolean success;
    private int id;
    private String reason;
    private Session auditAgentSession;
    @Override
    public byte getMessageType() {
        return MessageType.ENROLL_INFORMATION_RESPONSE;
    }
}
