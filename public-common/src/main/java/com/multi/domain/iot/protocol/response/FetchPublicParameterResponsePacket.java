package com.multi.domain.iot.protocol.response;

import com.multi.domain.iot.parameter.TransmitPublicParameter;
import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.response
 * @Author: duwei
 * @Date: 2022/11/17 11:32
 * @Description: 获取公共参数响应数据包
 */
@Data
public class FetchPublicParameterResponsePacket extends Packet {
    private TransmitPublicParameter transmitPublicParameter;
    @Override
    public byte getMessageType() {
        return MessageType.FETCH_PUBLIC_PARAMETER_RESPONSE_PACKET;
    }
}
