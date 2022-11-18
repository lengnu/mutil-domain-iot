package com.multi.domain.iot.protocol.response;

import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.message.MessageType;
import lombok.Data;

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
    @Override
    public byte getMessageType() {
        return MessageType.ENROLL_INFORMATION_RESPONSE;
    }
}
