package com.multi.domain.iot.common.protocol.response;

import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.response
 * @Author: duwei
 * @Date: 2022/11/21 13:00
 * @Description: UD发送验证消息到ID-Verifier响应数据报
 */
@Data
public class UDAuthenticationMessageResponsePacket extends Packet {
    private boolean success;
    private String reason;

    @Override
    public byte getMessageType() {
        return MessageType.UD_AUTHENTICATION_MESSAGE_RESPONSE;
    }
}
