package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.entity.UDAllVerifyInformation;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.request
 * @Author: duwei
 * @Date: 2022/11/21 13:00
 * @Description: UD发送验证消息到ID-Verifier请求数据报
 */
@Data
public class UDDeliverVerifyInformationToIDVerifierRequestPacket extends Packet {
    private UDAllVerifyInformation udAllVerifyInformation;

    @Override
    public byte getMessageType() {
        return MessageType.UD_DELIVER_VERIFY_INFORMATION_TO_ID_VERIFIER_REQUEST;
    }
}
