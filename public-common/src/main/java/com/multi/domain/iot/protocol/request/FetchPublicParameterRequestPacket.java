package com.multi.domain.iot.protocol.request;

import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.request
 * @Author: duwei
 * @Date: 2022/11/17 11:29
 * @Description: 获取公共参数请求数据包
 */
@Data
public class FetchPublicParameterRequestPacket extends Packet {
    @Override
    public byte getMessageType() {
        return MessageType.FETCH_PUBLIC_PARAMETER_REQUEST_PACKET;
    }
}
